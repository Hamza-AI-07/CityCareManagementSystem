
package com.example.municipalservices;

import com.example.municipalservices.utils.ValidationUtils;

import org.junit.Test;
import static org.junit.Assert.*;

public class ValidationUtilsTest {
    @Test
    public void isValidEmail_validEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("test@example.com"));
    }

    @Test
    public void isValidEmail_invalidEmail_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("invalid-email"));
    }

    @Test
    public void isValidName_shortName_returnsFalse() {
        assertFalse(ValidationUtils.isValidName("ab"));
    }

    @Test
    public void isValidName_validName_returnsTrue() {
        assertTrue(ValidationUtils.isValidName("John Doe"));
    }
}
