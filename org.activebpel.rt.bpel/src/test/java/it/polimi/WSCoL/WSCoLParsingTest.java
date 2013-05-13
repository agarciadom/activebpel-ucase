package it.polimi.WSCoL;

import java.io.StringReader;

import org.junit.Test;
import static org.junit.Assert.assertTrue;

/**
 * Tests for the WSCoL/WSReL parser.
 *
 * @author Antonio García-Domínguez
 */
public class WSCoLParsingTest {

	@Test
	public void monitoringRule() throws Exception {
		parseRule("let $hRes = returnNum('http://127.0.0.1:8080/ImageVerifierServiceBeanService/ImageVerifierServiceBean?wsdl', 'getHRes','<InvokeServiceParameters><imageURL>' + $MapService_getMapResponse/result + '</imageURL></InvokeServiceParameters>', /Response/result); $hRes <= 150;");
	}

	@Test
	public void recoveryRule() throws Exception {
		final String rule = "if($hRes < 180;){ignore()}else{change_supervision_rules(\"let $hRes = returnNum('http://127.0.0.1:8080/ImageVerifierServiceBeanService/ImageVerifierServiceBean?wsdl','getHRes','<InvokeServiceParameters><imageURL>' + $MapService_getMapResponse/result + '</imageURL></InvokeServiceParameters>',/Response/result);$hRes <= 750;\",\"{ignore() and notify('The map is still too big.','mac@localhost')}\",'permanent')}";
		parseRule(rule);
	}
	
	private void parseRule(final String rule) throws Exception {
		final WSCoLLexer lexer = new WSCoLLexer(new StringReader(rule));
		final WSCoLParser parser = new WSCoLParser(lexer);
		parser.analyzer();
		assertTrue(parser.getErrors().isEmpty());
	}
}
