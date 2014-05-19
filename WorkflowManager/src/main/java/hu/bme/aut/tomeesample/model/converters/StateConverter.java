package hu.bme.aut.tomeesample.model.converters;

import hu.bme.aut.tomeesample.service.StateService;

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
public class StateConverter implements Converter {
	// private List<ActionType> actionTypes;

	// public ActionTypeConverter() {
	// this.actionTypes = MyController.getAllMyObjects();
	// }

	@Inject
	private StateService stateService;

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String value) {

		// ActionType actionType =actionTypeService.findById(id);
		// if (object instanceof ActionType) {
		// return (ActionType) object;
		// }

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
		return stateService.findById(id);
		// return value;
	}

	@Override
	public String getAsString(FacesContext context, UIComponent component, Object value) {
		if (value == null) {
			return null;
		}
		return value.toString();
	}

	// public MyObject getMyObject(String id) {
	// Iterator<MyObject> iterator = this.objects.iterator();
	// while (iterator.hasNext()) {
	// MyObject object = iterator.next();
	//
	// if (object.getId() == Integer.valueOf(id).intValue()) {
	// return object;
	// }
	// }
	// return null;
	// }
}
