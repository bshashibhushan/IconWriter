package com.ikon.servlet.frontend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ikon.api.OKMDocument;
import com.ikon.api.OKMFolder;
import com.ikon.api.OKMRepository;
import com.ikon.bean.ContentInfo;
import com.ikon.bean.Document;
import com.ikon.bean.Folder;
import com.ikon.bean.Version;
import com.ikon.core.AccessDeniedException;
import com.ikon.core.DatabaseException;
import com.ikon.core.PathNotFoundException;
import com.ikon.core.RepositoryException;
import com.ikon.frontend.client.OKMException;
import com.ikon.frontend.client.constants.service.ErrorCode;
import com.ikon.util.FileUtils;
import com.ikon.util.PathUtils;
import com.ikon.util.WebUtils;

public class ExportToExcelServlet extends OKMHttpServlet {

	/**
	 * Export TO Excel
	 */
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(DownloadServlet.class);

	@Override
	protected void service(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		log.debug("service({}, {})", request, response);
		request.setCharacterEncoding("UTF-8");
		String id = request.getParameter("id");
		String path = id != null ? new String(id.getBytes("ISO-8859-1"),
				"UTF-8") : null;
		String uuid = request.getParameter("uuid");
		String checkout = request.getParameter("checkout");
		String ver = request.getParameter("ver");
		boolean inline = request.getParameter("inline") != null;
		boolean exportToExcel = request.getParameter("exportToExcel") != null;
		File tmp = File.createTempFile("okm", ".tmp");
		Document doc = null;
		InputStream is = null;
		updateSessionManager(request);

		try {
			// Now an document can be located by UUID
			if (uuid != null && !uuid.equals("")) {
				path = OKMRepository.getInstance().getNodePath(null, uuid);
			}

			if (exportToExcel) {
				FileOutputStream outputStream = new FileOutputStream(tmp);
				exportToExcel(path, outputStream);
				outputStream.flush();
				outputStream.close();
				is = new FileInputStream(tmp);

				// send document
				String fileName = PathUtils.getName(path) + ".xls";
				WebUtils.sendFile(request, response, fileName,
						"application/vnd.ms-excel", inline, is);
			} else {
				// Get document
				doc = OKMDocument.getInstance().getProperties(null, path);

				if (ver != null && !ver.equals("")) {
					is = OKMDocument.getInstance().getContentByVersion(null,
							path, ver);
				} else {
					is = OKMDocument.getInstance().getContent(null, path,
							checkout != null);
				}

				// Send document
				String fileName = PathUtils.getName(doc.getPath());
				WebUtils.sendFile(request, response, fileName,
						doc.getMimeType(), inline, is);
			}
		} catch (PathNotFoundException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(
					ErrorCode.ORIGIN_OKMDownloadService,
					ErrorCode.CAUSE_PathNotFound), e.getMessage()));
		} catch (RepositoryException e) {
			log.warn(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(
					ErrorCode.ORIGIN_OKMDownloadService,
					ErrorCode.CAUSE_Repository), e.getMessage()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(
					ErrorCode.ORIGIN_OKMDownloadService, ErrorCode.CAUSE_IO),
					e.getMessage()));
		} catch (DatabaseException e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(
					ErrorCode.ORIGIN_OKMDownloadService,
					ErrorCode.CAUSE_Database), e.getMessage()));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new ServletException(new OKMException(ErrorCode.get(
					ErrorCode.ORIGIN_OKMDownloadService,
					ErrorCode.CAUSE_General), e.getMessage()));
		} finally {
			IOUtils.closeQuietly(is);
			FileUtils.deleteQuietly(tmp);
		}

		log.debug("service: void");
	}

	/**
	 * Actual Export To Excel 
	 * @param path
	 * @param outputStream
	 * @throws ServletException
	 * @throws IOException
	 * @throws AccessDeniedException
	 * @throws RepositoryException
	 * @throws PathNotFoundException
	 * @throws DatabaseException
	 */
	private void exportToExcel(final String path,
			final OutputStream outputStream) throws ServletException,
			IOException, AccessDeniedException, RepositoryException,
			PathNotFoundException, DatabaseException {

		log.debug("exportExcel({}, {})", path, outputStream);
		OKMFolder okmFolder = OKMFolder.getInstance();

		ContentInfo contentInfo = okmFolder.getContentInfo(null, path);
		Folder folder = okmFolder.getProperties(null, path);
		List<Folder> folders = okmFolder.getChildren(null, path);
		OKMDocument okmDocument = OKMDocument.getInstance();
		List<Document> documents = okmDocument.getChildren(null, path);

		HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
		HSSFFont hssfFont = hssfWorkbook.createFont();
		HSSFCellStyle hssfCellStyle = hssfWorkbook.createCellStyle();

		hssfFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		hssfFont.setFontName("Calibri");
		hssfFont.setBoldweight(HSSFFont.COLOR_RED);

		hssfCellStyle.setFillBackgroundColor(HSSFColor.LIGHT_GREEN.index);
		hssfCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER_SELECTION);
		hssfCellStyle.setFont(hssfFont);

		HSSFSheet hssfSheet = hssfWorkbook.createSheet("Folder Name - " + path.substring(path.lastIndexOf("/")+1));

		int rowCount = 0;
		int columnCount = 0;
		int tempVariable = 0;
		boolean isExcelPending = true;

		HSSFCell hssfCell;
		HSSFRow hssfRow;

		/**
		 * Loop to parse all document list to Excel
		 */
		while (isExcelPending) {

			if (rowCount == 0) {
				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellStyle(hssfCellStyle);
				hssfCell.setCellValue("tShare Document Details");
				rowCount++;
			}
			if (rowCount == 1) {
				columnCount = 0;
				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Details of the Folder");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(path.substring(path.lastIndexOf('/') + 1));
				rowCount++;
			} else if (rowCount == 2 || rowCount == 6 || rowCount == 10) {
				columnCount = 0;
				rowCount++;
			} else if (rowCount == 3) {
				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Size of the Folder");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(contentInfo.getSize() + "kb");
				rowCount++;
				columnCount = 0;

				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Number of Sub-folders");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(contentInfo.getFolders());
				rowCount++;
				columnCount = 0;

				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Number of Documents");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(contentInfo.getDocuments());
				rowCount++;
				columnCount = 0;
			} else if (rowCount == 7) {
				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellStyle(hssfCellStyle);
				hssfCell.setCellValue("Details of the Current Folder");
				rowCount++;

				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Name of the Document");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(folder.getPath().substring(
						folder.getPath().lastIndexOf('/') + 1));
				rowCount++;
				columnCount = 0;

				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Author of the Document");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(folder.getAuthor());
				rowCount++;
				columnCount = 0;

				hssfRow = hssfSheet.createRow(rowCount);
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue("Created on");
				columnCount++;
				hssfCell = hssfRow.createCell(columnCount);
				hssfCell.setCellValue(folder.getCreated().getTime().toString());
				rowCount++;
				rowCount++;
				columnCount = 0;
			} else if (rowCount == 12) {
				columnCount = 0;
				if (folders != null) {
					hssfRow = hssfSheet.createRow(rowCount);
					hssfCell = hssfRow.createCell(columnCount);
					hssfCell.setCellStyle(hssfCellStyle);
					hssfCell.setCellValue("Details of the Sub-folder(s)");
					rowCount++;

					tempVariable = 0;

					if (folders.size() != 0) {
						for (Folder folder2 : folders) {
							if (tempVariable == 0) {
								hssfRow = hssfSheet.createRow(rowCount);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue("Name of the Document");
								rowCount++;
								hssfRow = hssfSheet.createRow(rowCount);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue("Author");
								rowCount++;
								hssfRow = hssfSheet.createRow(rowCount);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue("Created on");

								columnCount++;
								rowCount++;
								rowCount++;

								hssfRow = hssfSheet.getRow(13);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getPath()
										.substring(
												folder2.getPath().lastIndexOf(
														'/') + 1));

								hssfRow = hssfSheet.getRow(13 + 1);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getAuthor());

								hssfRow = hssfSheet.getRow(13 + 2);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getCreated()
										.getTime().toString());

								columnCount++;
								columnCount++;
								tempVariable++;

							} else {
								hssfRow = hssfSheet.getRow(13);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getPath()
										.substring(
												folder2.getPath().lastIndexOf(
														'/') + 1));

								hssfRow = hssfSheet.getRow(13 + 1);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getAuthor());

								hssfRow = hssfSheet.getRow(13 + 2);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(folder2.getCreated()
										.getTime().toString());
								columnCount++;
								columnCount++;
							}
						}
					} else {
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Name of the Document");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Author");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Created on");
						
						rowCount = 17; 
					}
				}
			} else {
				columnCount = 0;
				if (documents != null) {

					hssfRow = hssfSheet.createRow(rowCount);
					hssfCell = hssfRow.createCell(columnCount);
					hssfCell.setCellStyle(hssfCellStyle);
					hssfCell.setCellValue("Details of the Document(s) inside the current folder");
					rowCount++;

					tempVariable = 0;

					Version version;

					if (documents.size() != 0) {
						for (Document document : documents) {
							if (tempVariable == 0) {
								
								@SuppressWarnings("serial")
								ArrayList<String> excelHeadings = new ArrayList<String>() {
								{
								    add("Name of the Document");
								    add("Author");
								    add("Created on");
								    add("Modified on");
								    add("Version Details");
								    add("Author");
								    add("Name");
								    add("Created");
								    add("Size");
								    add("Comment");
								}};

								/**
								 * To write first heading
								 */
								for(String excelSample : excelHeadings){
									hssfRow = hssfSheet.createRow(rowCount);
									hssfCell = hssfRow.createCell(columnCount);
									hssfCell.setCellValue(excelSample);
									rowCount++;								
								}
								columnCount++;

								version = document.getActualVersion();
								hssfRow = hssfSheet.getRow(18);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getPath()
										.substring(
												document.getPath().lastIndexOf(
														'/') + 1));

								hssfRow = hssfSheet.getRow(18 + 1);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getAuthor());

								hssfRow = hssfSheet.getRow(18 + 2);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getCreated()
										.getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 3);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document
										.getLastModified().getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 5);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getAuthor());

								hssfRow = hssfSheet.getRow(18 + 6);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getName());

								hssfRow = hssfSheet.getRow(18 + 7);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getCreated()
										.getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 8);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getSize() + "kb");

								hssfRow = hssfSheet.getRow(18 + 9);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getComment());
								columnCount++;
								columnCount++;
								tempVariable++;

							} else {
								version = document.getActualVersion();
								hssfRow = hssfSheet.getRow(18);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getPath()
										.substring(
												document.getPath().lastIndexOf(
														'/') + 1));

								hssfRow = hssfSheet.getRow(18 + 1);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getAuthor());

								hssfRow = hssfSheet.getRow(18 + 2);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document.getCreated()
										.getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 3);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(document
										.getLastModified().getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 5);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getAuthor());

								hssfRow = hssfSheet.getRow(18 + 6);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getName());

								hssfRow = hssfSheet.getRow(18 + 7);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getCreated()
										.getTime().toString());

								hssfRow = hssfSheet.getRow(18 + 8);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getSize() + "kb");

								hssfRow = hssfSheet.getRow(18 + 9);
								hssfCell = hssfRow.createCell(columnCount);
								hssfCell.setCellValue(version.getComment());
								rowCount++;
								rowCount++;
								columnCount++;
								columnCount++;

							}

						}
					} else {
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Name of the Document");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Author");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Created on");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Modified on");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellStyle(hssfCellStyle);
						hssfCell.setCellValue("Version Details");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Author");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Name");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Created");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Size");
						rowCount++;
						hssfRow = hssfSheet.createRow(rowCount);
						hssfCell = hssfRow.createCell(columnCount);
						hssfCell.setCellValue("Comments");
					}

				}
				isExcelPending = false;
			}
		}
		hssfWorkbook.write(outputStream);
		log.info("Excel Exported");
		log.debug("exportExcel: void");
		return;
	}
}
