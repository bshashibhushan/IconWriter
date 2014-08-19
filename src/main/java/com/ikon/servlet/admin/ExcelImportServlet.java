package com.ikon.servlet.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.ikon.api.OKMFolder;
import com.ikon.api.OKMProperty;
import com.ikon.bean.Folder;
import com.ikon.core.Config;
import com.ikon.core.DatabaseException;
import com.ikon.core.ItemExistsException;
import com.ikon.core.ParseException;
import com.ikon.core.RepositoryException;
import com.ikon.dao.NodeBaseDAO;
import com.ikon.util.impexp.metadata.CategoryMetadata;
import com.ikon.util.impexp.metadata.DocumentMetadata;
import com.ikon.util.impexp.metadata.MetadataAdapter;
import com.ikon.util.impexp.metadata.PropertyGroupMetadata;
import com.ikon.util.impexp.metadata.PropertyMetadata;
 
public class ExcelImportServlet extends HttpServlet {
     
    private static final long serialVersionUID = 1L;
	private final OKMFolder okmFolder = OKMFolder.getInstance();
	private final static File TEMP_EXCEL_FILE = new File(Config.HOME_DIR + "/temp/" + "tmpexcel.xls");
	private int uploadedDocumentsCount = 0, count = 0;
	List<Integer> filesNotFound = null;
	List<String> nameOfDoc = null;
	int updatedDocuments = 0;
	     
	@SuppressWarnings({ "unchecked" })
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
       
        FileItemFactory factory = new DiskFileItemFactory();
        ServletFileUpload fileUpload  = new ServletFileUpload(factory);
        PrintWriter out = response.getWriter();
                
        try { 
            List<FileItem> items = fileUpload.parseRequest(request); 
            if(items.iterator().next().getName().endsWith(".xls") || items.iterator().next().getName().endsWith(".xlsx")) {
	            items.iterator().next().write(TEMP_EXCEL_FILE);
	            response.setHeader("Content-Type", "text/html");
	            
	            Workbook wb = WorkbookFactory.create(new FileInputStream(TEMP_EXCEL_FILE)); 
	            Sheet sheet = wb.getSheetAt(0);
	           
	            validateExcel(sheet, request, response); 
	           	readFromExcel(sheet, request, response);
	            
	            out.println("<hr/>");
	            out.println("<b> Number of documents imported: </b> " + uploadedDocumentsCount + "<br/>");
	        	out.println("<b> Number of documents updated: </b> " + updatedDocuments + "<br/>");
	        	out.println("<b> Documents failed to be imported: </b> " + count +"<br/>");	
	        	out.println("<b> List of those files: <b> " + "<br/>");
	        	
	        	for(int i = 0; i < nameOfDoc.size(); i++) {
	        		 out.println(i + ". " + nameOfDoc.get(i) + "<br/>");
	        	}     
	        	
				out.println("<hr/>");
				out.println("<a href=\"repository_excel_import.jsp\" align = \"center\">return</a>");
				out.println("<hr/>");
            }
            else {
            	request.setAttribute("ERROR", "Please upload excel file only");
    			request.getRequestDispatcher("/admin/repository_excel_import.jsp").forward(request, response);
            }
        } catch (Exception e) {
			e.printStackTrace();			
		} finally {
			request.setAttribute("NUMBER", uploadedDocumentsCount);
			updatedDocuments = 0;
			TEMP_EXCEL_FILE.delete();
		}
   	}
   
   	private void validateExcel(Sheet sheet, HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
   		for (Row row : sheet) {
   			if(row.getRowNum() == 0) {
   				continue;
   			}
   			  
   			for(int i = 0; i < 3 ; i++) {
   				if(row.getCell(i) == null) { 
   					request.setAttribute("ERROR", "Excel contains empty cells. Column " + i + " in Row " + row.getRowNum() + " is empty");
					request.getRequestDispatcher("/admin/repository_excel_import.jsp").forward(request, response);
   					break;
   				} 
   			}  	
   			
   			if(row.getCell(0).equals("end"))
   				break;
   		}
   	} 

	private void readFromExcel(Sheet sheet, HttpServletRequest request, HttpServletResponse response) throws Exception {
		  MetadataAdapter ma = MetadataAdapter.getInstance(null);
		  DocumentMetadata dmd = new DocumentMetadata();
		  nameOfDoc = new ArrayList<String>();
		  filesNotFound = new ArrayList<Integer>();
		  count = 0;
		  uploadedDocumentsCount = 0;
		   
		  for (Row row : sheet) {
			 if(row.getRowNum() == 0)
				 continue;
			 
			try{	     	
				 dmd.setPath(getDocPath(row));
				 dmd.setTitle(getCellValue(row, 0));
				 dmd.setKeywords(getKeywords(row));
				 dmd.setCategories(getCategories(row));
				 dmd.setPropertyGroups(getProperties(row));
				 
				 ma.importWithMetadata(dmd, new FileInputStream(getFSDocPath(row)));
				 uploadedDocumentsCount++;
			} catch (ItemExistsException e) {
				for(String str : getKeywords(row)) {
					if(str.length() > 1) {
						OKMProperty.getInstance().addKeyword(null, getDocPath(row), str);
					}
				}
				updatedDocuments++;
				continue;
			} catch (FileNotFoundException e) {
				count++;
				filesNotFound.add(count);
				nameOfDoc.add(getFSDocPath(row));
				continue;
			} finally {
				
			}
		}
	}
	
	private List<PropertyGroupMetadata> getProperties(Row row) throws ParseException, IOException, RepositoryException, DatabaseException {
		List<PropertyGroupMetadata> propertyGrpMetaData = new ArrayList<PropertyGroupMetadata>();
		List<PropertyMetadata> properties = new ArrayList<PropertyMetadata>();
		PropertyGroupMetadata pgmd = new PropertyGroupMetadata();
		
		if(row.getCell(5) != null) {		
			String propertyGrpName = row.getCell(5).getStringCellValue().toLowerCase();
			String documentTypeName = "okg:" + propertyGrpName;	                   
			pgmd.setName(documentTypeName);
        
	       	for(int i=6; i<20; i++) {
	       		PropertyMetadata pm = new PropertyMetadata();
	   			
	       		if(row.getCell(i) != null) {
		       		StringTokenizer str = new StringTokenizer(row.getCell(i).getStringCellValue(), ",");
		       		while(str.hasMoreTokens()) {
		       			pm.setMultiValue(false);
		       			pm.setName("okp:" + propertyGrpName + "." + str.nextToken().toLowerCase().replace(" ", ""));
		       			pm.setValue((String) str.nextElement());
		       			properties.add(pm);
		       		}
		       	}
	       	}
	       	pgmd.setProperties(properties);
			propertyGrpMetaData.add(pgmd);
       	}
		return propertyGrpMetaData;
	}

	private Set<CategoryMetadata> getCategories(Row row) throws Exception {
		CategoryMetadata category = new CategoryMetadata();
		Set<CategoryMetadata> categories = new HashSet<CategoryMetadata>();
		
		if(row.getCell(4)!=null) {
			for(String tCategoryPath : row.getCell(4).getStringCellValue().split(",")) {
				checkFolder(tCategoryPath, false);
				category.setUuid(NodeBaseDAO.getInstance().getUuidFromPath(getCompletePath(row, tCategoryPath, false)));
				category.setPath(getCompletePath(row, tCategoryPath, false));
				categories.add(category);
       		}
		}
		return categories;
	}

	private Set<String> getKeywords(Row row) {
	    Set<String> keywords = new HashSet<String>();
	    if(row.getCell(3)!=null) {
	    	 for(String keyword : row.getCell(3).getStringCellValue().split(",")) {
	 	    	keywords.add(keyword);
	 	    }
	    } 
		return keywords;
	}

	private String getDocPath(Row row) throws Exception {
		String docParentPath = row.getCell(2).getStringCellValue();
		checkFolder(docParentPath, true);
		return getCompletePath(row, docParentPath, true);
	}

	private String getCellValue(Row row, int index) {		
		return row.getCell(index).getStringCellValue();
	}

	private String getFSDocPath(Row row){
		return row.getCell(1).getStringCellValue() + "/" + row.getCell(0).getStringCellValue();		
	}
	
	private void checkFolder(final String docParentPath, boolean isRoot) throws Exception {
		String temp = "";
		
		if(isRoot)
			temp = "/Infodocs:root";
		else
			temp = "/Infodocs:categories";
		
		StringTokenizer str = new StringTokenizer(docParentPath, "/");
		while(str.hasMoreTokens()){
			try {
				temp = temp + "/" + str.nextToken();
				okmFolder.isValid(null, temp);
			} catch (Exception e) {
				Folder folder = new Folder();
				folder.setPath(temp);
				okmFolder.create(null, folder);
			}
		}		 
	}
	
	private String getCompletePath(Row row, String path, boolean isRoot) {
		StringBuilder string = new StringBuilder();
		string.append("/openkm:root/");
		string.append(path);
		string.append("/");
		if(isRoot) {
			string.append(row.getCell(0).getStringCellValue());
		} else {
			return "/openkm:categories/" + path;
		}
		return string.toString();
	}
	
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
}