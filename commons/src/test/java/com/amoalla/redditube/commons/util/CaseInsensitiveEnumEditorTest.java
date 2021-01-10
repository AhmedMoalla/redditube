package com.amoalla.redditube.commons.util;

import com.amoalla.redditube.api.dto.Sort;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CaseInsensitiveEnumEditorTest {

    @Test
    void testSetAsText() {
        CaseInsensitiveEnumEditor editor = new CaseInsensitiveEnumEditor(Sort.class);
        editor.setAsText("new");
        assertEquals(Sort.NEW, editor.getValue());

        editor.setAsText(null);
        assertNull(editor.getValue());
    }

}