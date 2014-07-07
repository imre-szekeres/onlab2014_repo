package hu.bme.aut.tomeesample.model.converters;

import hu.bme.aut.tomeesample.service.ActionTypeService;

import javax.ejb.Stateless;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@Stateless
@RequestScoped
public class ActionTypeConverter implements Converter {

	@Inject
	private ActionTypeService actionTypeService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		String idString = null;
		Long id = null;

		if (value.contains("id")) {
			int indexBegin = value.indexOf('|');
			int indexEnd = value.indexOf('|', indexBegin + 1);
			idString = value.substring(indexBegin + 1, indexEnd);
			System.out.println(idString);
		}
		try {
			id = Long.valueOf(idString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(value);
		return actionTypeService.findById(id);
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}
}
