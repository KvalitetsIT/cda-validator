package dk.medcom.cda.test.cpd.validation.dk;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import dk.medcom.cda.CollectingValidationHandler;
import dk.medcom.cda.model.ValidationEntry;
import dk.medcom.cda.validation.ICDARule;
import dk.medcom.cda.validation.pdc.dk.RuleConfPDCDkConfidentialAddress;
import org.junit.Before;
import org.junit.Test;
import org.openehealth.ipf.modules.cda.CDAR2Parser;
import org.openhealthtools.mdht.uml.cda.ClinicalDocument;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class RuleConfPDCDkConfidentialAddressTest {

  private static final String ADDRESS_ENTRY_ROOT_ID = "1.2.208.184.16.1.10.20.1.26";
  private CollectingValidationHandler validationHandler;

  private ClinicalDocument validClinicalDocument;
  private ClinicalDocument invalidClinicalDocument;

  @Before
	public void before() throws IOException {
		validationHandler = new CollectingValidationHandler();

    final String validDocument = Resources.toString(new URL(
                    "file:src/test/resources/PDC/PDC20_Example-5_2020-01-07_with_narrative_text_and_confidential_address_2.xml"),
            Charsets.UTF_8);
    final String invalidDocument = Resources.toString(new URL(
                    "file:src/test/resources/PDC/INVALID_PDC20_Example-5_2020-01-07_with_narrative_text_and_confidential_address_2.xml"),
            Charsets.UTF_8);

    validClinicalDocument = new CDAR2Parser().parse(validDocument, new Object[] {});
    invalidClinicalDocument = new CDAR2Parser().parse(invalidDocument, new Object[] {});
	}

	@Test
	public void testValidConfidentialAddressSetup() {
		final ICDARule rule = new RuleConfPDCDkConfidentialAddress();

		final boolean res = rule.validate(validClinicalDocument, validationHandler);

		assertThat(res, is(true));
		assertThat(getInfos().size(), is(0));
		assertThat(getWarnings().size(), is(0));
		assertThat(getErrors().size(), is(0));
	}

  @Test
  public void testInvalidConfidentialAddressHandling() {
    final ICDARule rule = new RuleConfPDCDkConfidentialAddress();

    final boolean res = rule.validate(invalidClinicalDocument, validationHandler);

    assertThat(res, is(true));
    assertThat(getInfos().size(), is(0));
    assertThat(getWarnings().size(), is(0));
    assertThat(getErrors().size(), is(4));
  }

	private List<ValidationEntry> getErrors() {
		return validationHandler.getDiagnostics().get(
				CollectingValidationHandler.Level.ERROR);
	}

	private List<ValidationEntry> getWarnings() {
		return validationHandler.getDiagnostics().get(
				CollectingValidationHandler.Level.WARNING);
	}

	private List<ValidationEntry> getInfos() {
		return validationHandler.getDiagnostics().get(
				CollectingValidationHandler.Level.INFO);
	}

}
