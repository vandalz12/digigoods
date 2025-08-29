# Hands-on: Increasing Coverage of Digigoods API with Augment Code

Welcome to the hands-on tutorial! You'll learn how to increase code coverage in the Digigoods API project using Augment Code.
This hands-on guide will give you outlines on the tasks you can follow to learn how to use Augment Code,
specifically for generating tests.

## Section 1: Introduction to AI-Generated Testing

First, let's examine the current test coverage to understand what needs improvement.

**Step 1**: Generate a fresh coverage report

```bash
./mvnw clean test jacoco:report
```

**Step 2**: Open the coverage report

```bash
open target/site/jacoco/index.html
```

**Checkpoint**: You should see the overall coverage.

### 1.2 Your First AI-Generated Test

Let's start by generating a test for the `DiscountService` class, which currently has no test coverage.

**Step 1**: Open Augment Code's chat mode (Cmd/Ctrl + Shift + A)

**Step 2**: Write a prompt to generate the tests.
```
I want to create unit tests for the DiscountService class. Please generate JUnit 5 tests that cover all methods including edge cases. The tests should:

- Follow the existing test patterns in the project
- Include tests for both success and failure scenarios
```

**Step 3**: Review the generated test code. Augment Code should create a test class similar to the existing test classes.

**Step 4**: Create the test file

- Create `src/test/java/com/example/digigoods/service/DiscountServiceTest.java`
- Copy the generated test code into the file

**Step 5**: Run the new tests

```bash
./mvnw test -Dtest=DiscountServiceTest
```

**Checkpoint**: All tests should pass. If any fail, use Augment Code's chat to debug and fix the issues.

### 1.3 Verifying Coverage Improvement

**Step 1**: Generate a new coverage report

```bash
./mvnw clean test jacoco:report
```

**Step 2**: Check the improvement in the coverage report

**Expected Result**: You should see improved coverage for the `DiscountService` class.

## Section 2: Setting Up Augment Code's Guidelines

### 2.1 Setting Up Guidelines

Augment Code's guidelines feature helps maintain consistency of AI-generated responses.
By default, Augment Code will read a text file named `.augment-guidelines.md` in the project root.
We will write the guidelines into this file.

**Step 1**: Create a New Guidelines File

- Open the project root
- Create an empty text file named `.augment-guidelines.md`
- Write some simple guidelines in the file. For example:

  ```markdown
  # Augment Guidelines

  ## Code Styles

  - Use Lombok to reduce boilerplate codes, especially when defining DTO classes.
  - Refer to [`.editorconfig`](./.editorconfig) for general code styles such as indentation, line length, end-of-line symbols.
  - Run `mvn checkstyle:check` to check for violations of Java code style. If violations found, read the report and try to fix them.
  ```

We will expand this file with more guidelines later.

### 2.2 Understanding Guidelines Benefits

Guidelines help Augment to:

- Maintain consistent coding patterns
- Follow your team's specific testing conventions
- Apply domain-specific testing patterns
- Ensure proper use of testing frameworks and libraries

**Key Benefits**:
- **Consistency**: The generated codes will follow the same patterns
- **Quality**: Guidelines enforce best practices
- **Context**: AI understands your project's specific needs
- **Efficiency**: Reduces manual review and modification time

## Section 3: Creating Custom Testing Rules

### 3.1 Writing Your First Testing Guideline

Let's create a simple but effective testing guideline for the project.

**Step 1**: Open the guidelines document, i.e., `.augment-guidelines.md`

**Step 2**: Add this basic testing rule as a subsection in the guidelines document:

```markdown
## Testing

- New code should have corresponding unit tests.
- Integration test of the API should be implemented using a Spring's `MockMvc` that can runs locally.
- Functional test of the API should be implemented using Postman collection that executed using `newman`.
- Make sure the tests still pass before and after making modification to the codebase.
- Use JUnit 5 test framework. To run the unit test suite, run `mvn test`. Make sure to run unit test suite before committing any code.
- Follow the convention of Arrange-Act-Assert (AAA) for writing tests, including providing short one-liner comments that denote the sections.
- Since we are using JUnit 5 as the test framework, please note the following when generating tests:
  - Name the test methods more verbosely. Write the test method name using Given-When-Then format.
  - Add `@DisplayName` annotation to all test methods and use descriptive names as the values of the annotation.
```

**Step 3**: Save the guidelines

### 3.2 Testing the New Guideline

Now let's test how the guideline affects AI-generated tests.

**Step 1**: Use Augment Code's chat mode to generate tests for `AuthService`:

```
Generate comprehensive unit tests for the AuthService class.
```

We do not need to explicitly include reference to the guidelines document in the prompt since Augment Code automatically include the content of the guidelines document. In addition, the guidelines document lets us to reduce redundancy in our prompt. Any rules that we often repeat in the prompts can be provided in the guidelines document.

**Step 2**: Compare the output with your previous test generation

- Notice how the structure follows your guidelines
- Check for consistent naming patterns
- Verify the use of annotations and test case naming convention

**Step 3**: Create the test file and run it

```bash
./mvnw test -Dtest=AuthServiceTest
```

**Checkpoint**: The generated test should follow your guidelines structure more closely than the first attempt.

## Section 4: Automated Coverage Improvement with Agent Mode

### 4.1 Introduction to Agent Mode

Augment Code's agent mode can autonomously analyze your codebase and generate tests to improve coverage.

**Key Features**:

- Autonomous analysis of uncovered code
- Intelligent test generation based on code complexity
- Integration with existing test patterns
- Iterative improvement approach

### 4.2 Using Agent Mode for Coverage Improvement

**Step 1**: Identify a specific file with low coverage

**Step 2**: Use this agent mode prompt (which was generated by Augment Code):

```
Please analyze the [ClassName] and automatically generate comprehensive unit tests to achieve 100% line coverage. Follow the existing testing guidelines and patterns in the project. Focus on:

1. All public methods and their edge cases
2. Exception handling scenarios
3. Complex business logic branches
4. Integration points with dependencies

After generating tests, run them to ensure they pass and verify the coverage improvement.
```

We can also provide our instructions to the agent as part of the guidelines document.
Open the guidelines document and add the following section:

```
## Code Coverage

- When asked to increase a specific source code file, check the coverage report of the said source code file first.
- The coverage report of a source code file can be found in `target/site/jacoco/[package name]/[class name].html`.
- When reading the coverage report, identify which lines and branches that yet to be covered.
- Make sure to run `mvn test` before and after adding tests.
```

**Step 3**: Let the agent work autonomously

- The agent will analyze the code
- Generate appropriate tests
- Create the test file
- Run tests to verify they pass
- Generate coverage reports to confirm improvement

**Step 4**: Review the agent's work

- Examine the generated test file
- Check test quality and coverage
- Verify adherence to guidelines

**Step 5**: Use agent mode for remaining uncovered classes

Apply agent mode to other service classes that need coverage improvement.

## Section 5: Hands-on Coverage Exercises

Now you have practiced how to set up guidelines for Augment Code and use the agent mode to improve coverage.
The remaining time in the hands-on session can be used to practice on other uncovered classes.
Please check the latest coverage report and identify which classes that still need coverage improvement.
You can use either chat mode or agent mode to improve the coverage.

## Conclusion

You have successfully learned how to leverage Augment Code's chat & agent capabilities to improve code coverage.
The combination of chat mode for targeted test generation, guidelines for consistency, and agent mode for autonomous improvement provides a powerful toolkit for maintaining high-quality test suites and easily increase code coverage.
However, please always keep in mind that AI/LLM have risks of hallucination, thus may generate incorrect code.
You, as human developer, still need to understand the generated code and have the fundamental skills for testing.
