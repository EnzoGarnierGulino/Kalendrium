package com.example.kalendrium;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.ComponentList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Summary;

public class IcsParser {

	public IcsParser() {
		System.setProperty("ical4j.unfolding.relaxed", "true");
	}

	public List<Cours> parseICSFile(String filename) {
		List<Cours> coursList = new ArrayList<>();

		try (FileInputStream fin = new FileInputStream(filename)) {
			CalendarBuilder builder = new CalendarBuilder();
			net.fortuna.ical4j.model.Calendar calendar = builder.build(fin);
			ComponentList components = calendar.getComponents(Component.VEVENT);

			for (Object component : components) {
				if (component instanceof VEvent) {
					VEvent vEvent = (VEvent) component;
					Cours cours = createCoursFromVEvent(vEvent);
					coursList.add(cours);
				}
			}
		} catch (IOException | ParserException e) {
			e.printStackTrace();
		}

		return coursList;
	}

	private Cours createCoursFromVEvent(VEvent vEvent) {
		DtStart startDate = vEvent.getStartDate();
		DtEnd endDate = vEvent.getEndDate();
		Summary summary = vEvent.getSummary();
		Description description = vEvent.getDescription();

		Map<String, String> detailsMap = extractDetailsFromDescription(description);

		Cours cours = new Cours();

		Calendar calendarStart = Calendar.getInstance();
		calendarStart.setTime(startDate.getDate());
		cours.setDateStart(calendarStart);
		
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.setTime(endDate.getDate());
		cours.setDateEnd(calendarEnd);

		cours.setMatiere(detailsMap.get("Matière"));
		cours.setEnseignant(detailsMap.get("Enseignant"));
		cours.setTd(detailsMap.get("TD"));
		cours.setPromotion(detailsMap.get("Promotion"));
		cours.setSalle(detailsMap.get("Salle"));
		cours.setType(detailsMap.get("Type"));
		cours.setMemo(detailsMap.get("Mémo"));
		cours.setSummary(summary != null ? summary.getValue() : "");

		return cours;
	}

	private Map<String, String> extractDetailsFromDescription(Description description) {
		Map<String, String> detailsMap = new HashMap<>();

		if (description != null) {
			for (String line : description.getValue().split("\n")) {
				String[] keyValue = line.split(":");
				if (keyValue.length == 2) {
					String key = keyValue[0].trim();
					String value = keyValue[1].trim();
					detailsMap.put(key, value);
				}
			}
		}

		return detailsMap;
	}
}
