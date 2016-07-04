package il.co.topq.difido.plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import il.co.topq.report.business.elastic.ElasticsearchTest;

public class TestTemplates {

	private TrendsMailPlugin plugin;

	@Before
	public void setUp() {
		plugin = new TrendsMailPlugin();
	}

	@Test
	public void testStatusTemplate()
			throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
		final ExecutionStatus currentExecutionStatus = new ExecutionStatus(100, "Sanity", 11, 2, 3);
		String body = plugin.populateStatusTemplate("execution description", currentExecutionStatus);
		Assert.assertNotNull(body);
		Assert.assertTrue(body.contains("100"));
		Assert.assertTrue(body.contains("11"));
		Assert.assertTrue(body.contains("2"));
		Assert.assertTrue(body.contains("3"));
		Assert.assertTrue(body.contains("execution description"));
	}

	@Test
	public void testComparisonTemplate()
			throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
		final ExecutionStatus currentExecutionStatus = new ExecutionStatus(100, "Sanity", 15, 0, 1);
		final ExecutionStatus previousExecutionStatus = new ExecutionStatus(101, "Sanity", 10, 5, 2);
		String body = plugin.populateComparisonTemplate(currentExecutionStatus, previousExecutionStatus);
		Assert.assertNotNull(body);
		Assert.assertTrue(body.contains("100"));
		Assert.assertTrue(body.contains("101"));
		Assert.assertTrue(body.contains("5"));
		Assert.assertTrue(body.contains("-5"));
		Assert.assertTrue(body.contains("-1"));

	}

	@Test
	public void testTestsTemplate()
			throws ParseErrorException, MethodInvocationException, ResourceNotFoundException, IOException {
		List<ElasticsearchTest> tests = new ArrayList<ElasticsearchTest>();
		int numOfTests = 10;
		// success, warning, failure, error
		for (int i = 0; i < numOfTests; i++) {
			ElasticsearchTest test = new ElasticsearchTest();
			test.setName("test" + i);
			test.setStatus(i % 2 == 0 ? "success" : "failure");
			Map<String, String> props = new HashMap<String, String>();
			props.put("failureReason", "Something bad happended");
			props.put("link", "http://www.google.com");
			props.put("foo", "bar");
			test.setProperties(props);
			tests.add(test);
		}
		final String body = plugin.populateTestsTemplate(tests);
		for (int i = 0; i < numOfTests; i++) {
			Assert.assertTrue(body.contains("test" + i));
		}
		Assert.assertTrue(body.contains("success"));
		Assert.assertTrue(body.contains("fail"));
		Assert.assertTrue(body.contains("Something bad happended"));
		Assert.assertTrue(body.contains("http://www.google.com"));
		Assert.assertFalse(body.contains("bar"));

	}

}
