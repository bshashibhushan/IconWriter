<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<fieldset>
  <legend>Menu tool</legend>
  <table>
    <tr>
      <td>Languages</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfTool.languagesVisible}">
            <input name="prf_menu_tool_languages_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_languages_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Skin</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfTool.skinVisible}">
            <input name="prf_menu_tool_skin_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_skin_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Administration</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfTool.administrationVisible}">
            <input name="prf_menu_tool_administration_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_administration_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
    <tr>
      <td>Preferences</td>
      <td>
        <c:choose>
          <c:when test="${prf.prfMenu.prfTool.preferencesVisible}">
            <input name="prf_menu_tool_preferences_visible" type="checkbox" checked="checked"/>
          </c:when>
          <c:otherwise>
            <input name="prf_menu_tool_preferences_visible" type="checkbox"/>
          </c:otherwise>
        </c:choose>
      </td>
    </tr>
  </table>
</fieldset>