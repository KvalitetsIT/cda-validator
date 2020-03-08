package dk.medcom.cda.validation.pdc.dk;

import dk.medcom.cda.CollectingValidationHandler;
import dk.medcom.cda.model.ValidationEntry;
import dk.medcom.cda.validation.ICDARule;
import org.eclipse.emf.common.util.EList;
import org.openhealthtools.mdht.uml.cda.*;
import org.openhealthtools.mdht.uml.hl7.datatypes.*;
import org.openhealthtools.mdht.uml.hl7.vocab.NullFlavor;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class RuleConfPDCDkConfidentialAddress implements ICDARule {

  private static final String CONF_PDC_DK_268 = "CONF-PDC-DK-268";
  private static final String CONF_PDC_DK_269 = "CONF-PDC-DK-269";
  private static final String CONF_PDC_DK_270 = "CONF-PDC-DK-270";
  private static final String CONF_PDC_DK_271 = "CONF-PDC-DK-271";
  private static final String ERROR_MESSAGE_CONF_DK_268_CONFIDENTIAL_ADDRESS = "If the citizen has a confidential address the first streetAddressLine shall contain “Adressebeskyttelse”and streetAddressLine#2, #3 and  #4 must not be present.";
  private static final String ERROR_MESSAGE_CONF_DK_269_CONFIDENTIAL_ADDRESS = "If the citizen has a confidential address the postalCode shall contain nullFlavor=NI";
  private static final String ERROR_MESSAGE_CONF_DK_270_CONFIDENTIAL_ADDRESS = "If the citizen has a confidential address the city shall contain nullFlavor=NI";
  private static final String ERROR_MESSAGE_CONF_DK_271_CONFIDENTIAL_ADDRESS = "If the citizen has a confidential address the country shall contain nullFlavor=NI";

  private static final String CITIZEN_NAME_ADDR = "CitizenNameAddr";
  private static final String MEDCOM_VALUESET = "1.2.208.184.100.1";
  private static final String CONF_ADDR = "ConfAddr";
  private static final String MED_COM = "MedCom";
  private static final String ADRESSEBESKYTTELSE = "Adressebeskyttelse";

  @Override
	public boolean validate(ClinicalDocument document,
			                    CollectingValidationHandler validationHandler) {

    final Optional<Entry> possibleCitizenNameAddrEntry = getNameAddressEntry(document);

    final Set<ANY> values = possibleCitizenNameAddrEntry.stream()
                              .flatMap(entry -> entry.getObservation().getValues().stream())
                              .collect(Collectors.toSet());

    final Set<II> valuesOfTypeII = getValuesOfType(II.class, values);
    final Set<AD> valuesOfTypeAD = getValuesOfType(AD.class, values);

    if(hasAConfidentialAddress(valuesOfTypeII)) {
      valuesOfTypeAD.forEach(address -> {
        validateStreetAddressLinesAccordingToConfidentialAddressRules(validationHandler, address);
        validatePostalcodeAccordingToConfidentialAddressRules(validationHandler, address);
        validateCityAccordingToConfidentialAddressRules(validationHandler, address);
        validateCountryAccordingToConfidentialAddressRules(validationHandler, address);
      });
    }

    return true;
	}

  private boolean hasAConfidentialAddress(Set<II> valuesOfTypeII) {
    return valuesOfTypeII.stream()
              .anyMatch(value -> MEDCOM_VALUESET.equals(value.getRoot()) &&
                                 CONF_ADDR.equals(value.getExtension()) &&
                                 MED_COM.equals(value.getAssigningAuthorityName()));
  }

  private Optional<Entry> getNameAddressEntry(ClinicalDocument document) {
    final EList<Section> sections = document.getSections();
    final Set<Entry> entries = sections.stream()
                                    .flatMap(section -> section.getEntries().stream())
                                    .collect(Collectors.toSet());

    //CONF-PDC-DK-165 guaranties that only one entry exists
    return entries.stream()
            .filter(entry -> CITIZEN_NAME_ADDR.equals(getCodeFromEntry(entry)))
            .findFirst();
  }

  private void validateCountryAccordingToConfidentialAddressRules(CollectingValidationHandler validationHandler, AD address) {
    address.getCountries().stream().forEach(country -> {
      if(country.getNullFlavor() != NullFlavor.NI) {
        validationHandler.handleError(new ValidationEntry(ERROR_MESSAGE_CONF_DK_271_CONFIDENTIAL_ADDRESS, null, CONF_PDC_DK_271));
      }
    });
  }

  private void validateCityAccordingToConfidentialAddressRules(CollectingValidationHandler validationHandler, AD address) {
    address.getCountries().stream().forEach(country -> {
      if(country.getNullFlavor() != NullFlavor.NI) {
        validationHandler.handleError(new ValidationEntry(ERROR_MESSAGE_CONF_DK_270_CONFIDENTIAL_ADDRESS, null, CONF_PDC_DK_270));
      }
    });
  }

  private void validatePostalcodeAccordingToConfidentialAddressRules(CollectingValidationHandler validationHandler, AD address) {
    address.getCountries().stream().forEach(country -> {
      if(country.getNullFlavor() != NullFlavor.NI) {
        validationHandler.handleError(new ValidationEntry(ERROR_MESSAGE_CONF_DK_269_CONFIDENTIAL_ADDRESS, null, CONF_PDC_DK_269));
      }
    });
  }

  private void validateStreetAddressLinesAccordingToConfidentialAddressRules(CollectingValidationHandler validationHandler, AD address) {
    final EList<ADXP> streetAddressLines = address.getStreetAddressLines();
    final ADXP firstAddressLine = !streetAddressLines.isEmpty() ? streetAddressLines.get(0) : null;
    if(streetAddressLines.size() == 1) {
      if(!ADRESSEBESKYTTELSE.equals(firstAddressLine.getText())) {
        validationHandler.handleError(new ValidationEntry(ERROR_MESSAGE_CONF_DK_268_CONFIDENTIAL_ADDRESS, null, CONF_PDC_DK_268));
      }
    } else {
      validationHandler.handleError(new ValidationEntry(ERROR_MESSAGE_CONF_DK_268_CONFIDENTIAL_ADDRESS, null, CONF_PDC_DK_268));
    }
  }

  private <T> Set<T> getValuesOfType(Class<T> clazz, Set<ANY> values) {
    return values.stream()
                .filter(clazz::isInstance)
                .map(value -> (T)value)
                .collect(Collectors.toSet());
  }

  private String getCodeFromEntry(Entry entry) {
    if(entry == null) return null;
    final Observation observation = entry.getObservation();
    if(observation == null) return null;
    final CD code = observation.getCode();
    if(code == null) return null;
    return code.getCode();
  }

}
