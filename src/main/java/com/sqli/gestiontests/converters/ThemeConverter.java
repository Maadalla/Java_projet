package com.sqli.gestiontests.converters;

import com.sqli.gestiontests.dao.ThemeDAO;
import com.sqli.gestiontests.entities.Theme;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter("themeConverter")
public class ThemeConverter implements Converter {

    private final ThemeDAO themeDAO = new ThemeDAO();

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value == null || value.trim().isEmpty() || value.equals("null")) {
            return null;
        }
        try {
            Long id = Long.parseLong(value);
            return themeDAO.findById(id);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        if (value == null) {
            return "";
        }
        if (value instanceof Theme) {
            return String.valueOf(((Theme) value).getId());
        }
        return value.toString();
    }
}
