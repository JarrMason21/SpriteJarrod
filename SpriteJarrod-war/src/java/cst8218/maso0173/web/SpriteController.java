package cst8218.maso0173.web;

import cst8218.maso0173.entity.Sprite;
import cst8218.maso0173.entity.SpriteFacade;
import cst8218.maso0173.web.util.JsfUtil;
import cst8218.maso0173.web.util.PaginationHelper;
import java.awt.Color;
import java.io.Serializable;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
This class is the named bean and it directly interacts with the JSF pages of the 
application. It is basically this connection between those JSF pages and the SpriteFacade
that allows the user to create, edit, view and delete Sprites on the JSF pages.
*/
@Named("spriteController")
@SessionScoped
public class SpriteController implements Serializable {

    private Sprite current;
    private DataModel items = null;
    @EJB
    private SpriteFacade ejbFacade;
    private PaginationHelper pagination;
    private int selectedItemIndex;

    public SpriteController() {
    }

    public Sprite getSelected() {
        if (current == null) {
            current = new Sprite();
            selectedItemIndex = -1;
        }
        return current;
    }

    private SpriteFacade getFacade() {
        return ejbFacade;
    }

    public PaginationHelper getPagination() {
        if (pagination == null) {
            pagination = new PaginationHelper(10) {

                @Override
                public int getItemsCount() {
                    return getFacade().count();
                }

                @Override
                public DataModel createPageDataModel() {
                    return new ListDataModel(getFacade().findRange(new int[]{getPageFirstItem(), getPageFirstItem() + getPageSize()}));
                }
            };
        }
        return pagination;
    }

    public String prepareList() {
        recreateModel();
        return "List";
    }

    public String prepareView() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "View";
    }

    public String prepareCreate() {
        current = new Sprite();
        selectedItemIndex = -1;
        return "Create";
    }

    public String create() {
        try {
            getFacade().create(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteCreated"));
            return prepareCreate();
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String prepareEdit() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        return "Edit";
    }

    public String update() {
        try {
            getFacade().edit(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteUpdated"));
            return "View";
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            return null;
        }
    }

    public String destroy() {
        current = (Sprite) getItems().getRowData();
        selectedItemIndex = pagination.getPageFirstItem() + getItems().getRowIndex();
        performDestroy();
        recreatePagination();
        recreateModel();
        return "List";
    }

    public String destroyAndView() {
        performDestroy();
        recreateModel();
        updateCurrentItem();
        if (selectedItemIndex >= 0) {
            return "View";
        } else {
            // all items were removed - go back to list
            recreateModel();
            return "List";
        }
    }

    private void performDestroy() {
        try {
            getFacade().remove(current);
            JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SpriteDeleted"));
        } catch (Exception e) {
            JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
        }
    }

    private void updateCurrentItem() {
        int count = getFacade().count();
        if (selectedItemIndex >= count) {
            // selected index cannot be bigger than number of items:
            selectedItemIndex = count - 1;
            // go to previous page if last page disappeared:
            if (pagination.getPageFirstItem() >= count) {
                pagination.previousPage();
            }
        }
        if (selectedItemIndex >= 0) {
            current = getFacade().findRange(new int[]{selectedItemIndex, selectedItemIndex + 1}).get(0);
        }
    }

    public DataModel getItems() {
        if (items == null) {
            items = getPagination().createPageDataModel();
        }
        return items;
    }

    private void recreateModel() {
        items = null;
    }

    private void recreatePagination() {
        pagination = null;
    }

    public String next() {
        getPagination().nextPage();
        recreateModel();
        return "List";
    }

    public String previous() {
        getPagination().previousPage();
        recreateModel();
        return "List";
    }

    public SelectItem[] getItemsAvailableSelectMany() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), false);
    }

    public SelectItem[] getItemsAvailableSelectOne() {
        return JsfUtil.getSelectItems(ejbFacade.findAll(), true);
    }

    public Sprite getSprite(java.lang.Long id) {
        return ejbFacade.find(id);
    }

    @FacesConverter(forClass = Sprite.class)
    public static class SpriteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SpriteController controller = (SpriteController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "spriteController");
            return controller.getSprite(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Sprite) {
                Sprite o = (Sprite) object;
                return getStringKey(o.getId());
            } else {
                throw new IllegalArgumentException("object " + object + " is of type " + object.getClass().getName() + "; expected type: " + Sprite.class.getName());
            }
        }

    }
    
    /*
    Depending on user input the color of the Sprite is determined here. This method converts the string into the Sprite color and returns it.
    Used by the JSF pages
    */
    @FacesConverter(forClass = Color.class)
    public static class ColorConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String color) {
            if (color == null || color.length() == 0) {
                return null;
            }
            
            Color convertedColor = null;
            
            switch(color.toLowerCase()){
            
                case "black":
                    convertedColor = Color.BLACK;
                    break;
                case "gray":
                    convertedColor = Color.GRAY;
                    break;
                case "darkgray":
                    convertedColor = Color.DARK_GRAY;
                    break;
                case "lightgray":
                    convertedColor = Color.LIGHT_GRAY;
                    break;
                case "cyan":
                    convertedColor = Color.CYAN;
                    break;
                case "green":
                    convertedColor = Color.GREEN;
                    break;
                case "red":
                    convertedColor = Color.RED;
                    break;
                case "blue":
                    convertedColor = Color.BLUE;
                    break;                 
                case "orange":
                    convertedColor = Color.ORANGE;
                    break;
                case "white":
                    convertedColor = Color.WHITE;
                    break;
                case "yellow":
                    convertedColor = Color.YELLOW;
                    break;
                case "magenta":
                    convertedColor = Color.MAGENTA;
                    break;
                case "pink":
                    convertedColor = Color.PINK;
                    break;               
            }
                
            return convertedColor;
        }

        /*
        Converts the color object into a string and returns it. Used by thew JSF pages
        */
        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            
            String convertedColor = object.toString();
            
            //Remove java.awt.color from the string
            convertedColor = convertedColor.substring(14);
           
            return convertedColor;
        }

    }
}
