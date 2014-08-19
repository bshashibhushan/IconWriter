DELETE FROM ikon_AUTO_METADATA;

-- VALIDATION
INSERT INTO ikon_AUTO_METADATA (AMD_AT, AMD_CLASS_NAME, AMD_NAME, AMD_GROUP, AMD_TYPE00, AMD_SRC00, AMD_DESC00, AMD_TYPE01, AMD_SRC01, AMD_DESC01) VALUES ('post', 'com.ikon.automation.validation.PathContains', 'PathContains', 'validation', 'text', 'okm:folder', 'String', '', '', '');

-- ACTIONS
INSERT INTO ikon_AUTO_METADATA (AMD_AT, AMD_CLASS_NAME, AMD_NAME, AMD_GROUP, AMD_TYPE00, AMD_SRC00, AMD_DESC00, AMD_TYPE01, AMD_SRC01, AMD_DESC01) VALUES ('post', 'com.ikon.automation.action.ExecuteScripting', 'ExecuteScripting', 'action', 'textarea', '', 'Script', '', '', '');

--------------------
-- ORACLE ----------
-- VALIDATION
INSERT INTO ikon_AUTO_METADATA (AMD_ID, AMD_AT, AMD_CLASS_NAME, AMD_NAME, AMD_GROUP, AMD_TYPE00, AMD_SRC00, AMD_DESC00, AMD_TYPE01, AMD_SRC01, AMD_DESC01) VALUES (HIBERNATE_SEQUENCE.nextval, 'post', 'com.ikon.automation.validation.PathContains', 'PathContains', 'validation', 'text', '', 'String', '', '', '');

-- ACTIONS
INSERT INTO ikon_AUTO_METADATA (AMD_ID, AMD_AT, AMD_CLASS_NAME, AMD_NAME, AMD_GROUP, AMD_TYPE00, AMD_SRC00, AMD_DESC00, AMD_TYPE01, AMD_SRC01, AMD_DESC01) VALUES (HIBERNATE_SEQUENCE.nextval, 'post', 'com.ikon.automation.action.ExecuteScripting', 'ExecuteScripting', 'action', 'textarea', '', 'Scripting', '', '', '');
