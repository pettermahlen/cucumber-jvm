package cucumber.junit;

import cucumber.io.ClasspathResourceLoader;
import cucumber.runtime.model.CucumberFeature;
import cucumber.runtime.model.CucumberScenario;
import gherkin.formatter.model.Step;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ExecutionUnitRunnerTest {
    @Test
    public void shouldAssignUnequalDescriptionsToDifferentOccurrencesOfSameStepInAScenario() throws Exception {
        List<CucumberFeature> features = CucumberFeature.load(
                new ClasspathResourceLoader(this.getClass().getClassLoader()),
                asList("cucumber/junit/feature_with_same_steps_in_scenario.feature"),
                Collections.emptyList()
        );

        ExecutionUnitRunner runner = new ExecutionUnitRunner(
                null,
                (CucumberScenario) features.get(0).getFeatureElements().get(0),
                null
        );

        // fish out the two occurrences of the same step and check whether we really got them
        Step stepOccurrence1 = runner.getChildren().get(0);
        Step stepOccurrence2 = runner.getChildren().get(2);
        assertEquals(stepOccurrence1.getName(), stepOccurrence2.getName());

        assertFalse("Descriptions must not be equal.", runner.describeChild(stepOccurrence1).equals(runner.describeChild(stepOccurrence2)));
    }

    @Test
    public void shouldIncludeScenarioNameAsClassNameInStepDescriptions()
        throws Exception {

        List<CucumberFeature> features = CucumberFeature.load(
            new ClasspathResourceLoader(this.getClass().getClassLoader()),
            asList("cucumber/junit/feature_with_same_steps_in_different_scenarios.feature"),
            Collections.emptyList()
        );

        ExecutionUnitRunner runner = new ExecutionUnitRunner(
            null,
            (CucumberScenario) features.get(0).getFeatureElements().get(0),
            null
        );

        // fish out the data from
        Step step = runner.getChildren().get(0);
        String expected = String.format("%s%s(%s)", step.getKeyword(), step.getName(), runner.getDescription());
        assertEquals("step includes scenario in junit-internal format", expected, runner.describeChild(step).toString().trim());
    }
}
