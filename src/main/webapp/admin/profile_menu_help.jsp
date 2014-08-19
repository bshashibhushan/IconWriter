<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu help</legend>
  <table>
    <tr>
      <td>Support</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.supportVisible}">
            <input name="prf_menu_help_support_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_support_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>About</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfHelp.aboutVisible}">
            <input name="prf_menu_help_about_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_help_about_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>
