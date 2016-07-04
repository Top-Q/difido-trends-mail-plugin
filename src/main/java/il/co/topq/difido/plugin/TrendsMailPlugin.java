package il.co.topq.difido.plugin;

import java.io.StringWriter;
import java.util.List;
import java.util.Scanner;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import il.co.topq.report.Common;
import il.co.topq.report.business.elastic.ESUtils;
import il.co.topq.report.business.elastic.ElasticsearchTest;
import il.co.topq.report.business.execution.ExecutionMetadata;
import il.co.topq.report.plugins.mail.DefaultMailPlugin;

/**
 * 
 * @author Itai Agmon
 *
 */
public class TrendsMailPlugin extends DefaultMailPlugin {

	private static final String EXECUTION_TYPE_PROPERTY_NAME = "Type";
	private final Logger log = LoggerFactory.getLogger(TrendsMailPlugin.class);

	@Override
	public String getName() {
		return "trendsMailPlugin";
	}

	private List<ElasticsearchTest> getAllTestsOfExecution(int executionId) throws Exception {
		log.debug("About to get all the tests of execution with id " + executionId + " from the Elastic");
		final List<ElasticsearchTest> tests = ESUtils.getAllByQuery(Common.ELASTIC_INDEX, "test",
				ElasticsearchTest.class, "executionId:" + executionId);
		if (null == tests || tests.isEmpty()) {
			throw new Exception("No tests found in execution " + executionId);
		}
		return tests;

	}

	@Override
	protected String getMailBody(ExecutionMetadata metadata) {
		try {
			// We need to give time to the Elastic to index the data
			Thread.sleep(2000);
		} catch (InterruptedException e1) {
		}
		if (null == metadata) {
			log.error("Metadata object is null");
			return "Metadata object is null";
		}
		final StringBuilder body = new StringBuilder();
		try {
			final int executionId = metadata.getId();
			log.debug("About to create mail for executin with id " + executionId);
			
			final String executionType = getExecutionType(executionId);
			log.debug("Type of execution with id " + executionId +" is " + executionType);
			
			final ExecutionStatus currentExecutionStatus = caluculateExecutionStatuses(executionId, executionType);
			log.debug("Status of execution with id " + executionId +" is " + currentExecutionStatus);
			
			body.append(populateStatusTemplate(metadata.getDescription(), currentExecutionStatus));

			if (null != executionType){
				int previousExecutionId = findPreviousExecutionId(executionId, executionType);
				if (0 != previousExecutionId) {
					final ExecutionStatus previousExecutionStatus = caluculateExecutionStatuses(previousExecutionId,
							executionType);
					log.debug("Status of previous execution with id " + previousExecutionId +" is " + previousExecutionStatus);
					
					body.append(populateComparisonTemplate(currentExecutionStatus, previousExecutionStatus));
				}
			}
			body.append(populateTestsTemplate(getAllTestsOfExecution(executionId)));

		} catch (Exception e) {
			log.error("Failure in creating mail due to " + e.getMessage());
			body.append("Failure in completing mail creation");
		}
		return body.toString();

	}

	String populateTestsTemplate(List<ElasticsearchTest> tests) {
		log.debug("Populating tests template");
		VelocityContext context = new VelocityContext();
		context.put("tests", tests);
		return populateTemplate(context, "tests.vm");
	}

	String populateStatusTemplate(String description, ExecutionStatus currentExecutionStatus) {
		log.debug("Populating status template for execution " + currentExecutionStatus.getId());
		VelocityContext context = new VelocityContext();
		context.put("description", description);
		context.put("current", currentExecutionStatus);
		return populateTemplate(context, "status.vm");
	}

	String populateComparisonTemplate(ExecutionStatus currentExecutionStatus, ExecutionStatus previousExecutionStatus) {
		log.debug("Populating comparing template for executions " + currentExecutionStatus.getId() +" and " + previousExecutionStatus.getId());
		VelocityContext context = new VelocityContext();
		context.put("current", currentExecutionStatus);
		context.put("previous", previousExecutionStatus);
		return populateTemplate(context, "comparison.vm");
	}

	private String populateTemplate(VelocityContext context, String templateName) {
		VelocityEngine ve = new VelocityEngine();
		ve.init();
		final StringWriter writer = new StringWriter();
		ve.evaluate(context, writer, "mailPlugin", resourceToString(templateName));
		return writer.toString();
	}

	private String resourceToString(final String resourceName) {
		try (Scanner s = new Scanner(getClass().getClassLoader().getResourceAsStream(resourceName))) {
			s.useDelimiter("\\A");
			return s.hasNext() ? s.next() : "";
		}
	}

	private int findPreviousExecutionId(int executionId, String executionType) throws Exception {
		double previoudExecutionId = ESUtils.maxFieldValueByQuery(Common.ELASTIC_INDEX, "test", "executionId",
				"scenarioProperties.Type:" + executionType + " AND executionId:<" + executionId);
		if (previoudExecutionId <= 0 || previoudExecutionId >= Integer.MAX_VALUE) {
			return 0;
		}
		return (int) previoudExecutionId;
	}

	private String getExecutionType(int executionId) throws Exception {
		final List<ElasticsearchTest> tests = getAllTestsOfExecution(executionId);
		final String type = tests.get(0).getScenarioProperties().get(EXECUTION_TYPE_PROPERTY_NAME);
		if (null == type || type.isEmpty()) {
			log.warn("Execution type is of execution with id " + executionId + " is missing");
			return null;
		}
		return type;
	}

	private ExecutionStatus caluculateExecutionStatuses(int id, String type) throws Exception {
		int success = 0;
		int failure = 0;
		int warning = 0;
		for (ElasticsearchTest test : getAllTestsOfExecution(id)) {
			switch (test.getStatus()) {
			case "success":
				success++;
				break;
			case "failure":
			case "error":
				failure++;
				break;
			case "warning":
				warning++;
				break;
			}
		}
		return new ExecutionStatus(id, type, success, failure, warning);
	}

}
