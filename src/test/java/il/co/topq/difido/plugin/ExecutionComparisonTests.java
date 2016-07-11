package il.co.topq.difido.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import il.co.topq.difido.model.Enums.Status;
import il.co.topq.report.business.elastic.ElasticsearchTest;

public class ExecutionComparisonTests {

	private TrendsMailPlugin trendsMailPlugin;
	private List<ElasticsearchTest> currentTests;
	private List<ElasticsearchTest> previousTests;

	@Before
	public void setup() {
		trendsMailPlugin = new TrendsMailPlugin();
		currentTests = new ArrayList<ElasticsearchTest>();
		previousTests = new ArrayList<ElasticsearchTest>();
	}

	@Test
	public void testCompareEquals() {
		
//		Creating tests. Fixture
		ElasticsearchTest test = new ElasticsearchTest();
		test.setStatus(Status.success.name());
		test.setName("mytest");
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("foo", "bar");
		test.setParameters(parameters);
		currentTests.add(test);
		previousTests.add(test);

		// Action
		ComparisonResult result = trendsMailPlugin.compareExecutions(currentTests, previousTests);
		
		// Assertion
		Assert.assertEquals(0, result.getFailToPass());
	}

}
