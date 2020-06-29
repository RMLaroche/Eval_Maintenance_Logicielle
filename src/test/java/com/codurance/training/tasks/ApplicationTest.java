package com.codurance.training.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ApplicationTest {
    public static final String PROMPT = "> ";
    private final PipedOutputStream inStream = new PipedOutputStream();
    private final PrintWriter inWriter = new PrintWriter(inStream, true);

    private final PipedInputStream outStream = new PipedInputStream();
    private final BufferedReader outReader = new BufferedReader(new InputStreamReader(outStream));

    private Thread applicationThread;

    public ApplicationTest() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new PipedInputStream(inStream)));
        PrintWriter out = new PrintWriter(new PipedOutputStream(outStream), true);
        App app = new App(in, out);
        applicationThread = new Thread(app);
    }

    @Before public void
    start_the_application() {
        applicationThread.start();
    }

    @After public void
    kill_the_application() throws IOException, InterruptedException {
        if (!stillRunning()) {
            return;
        }

        Thread.sleep(1000);
        if (!stillRunning()) {
            return;
        }

        applicationThread.interrupt();
        throw new IllegalStateException("The application is still running.");
    }

    @Test(timeout = 1000) public void
    addProjectTest() throws IOException {
        execute("add project secrets");
        execute("show");
        readLines(
                "secrets",
                ""
        );
        execute("quit");

    }
    @Test(timeout = 1000) public void
    addTaskTest() throws IOException {
        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        execute("show");
        readLines(
                "secrets",
                "    [ ] 1: Eat more donuts.",
                "    [ ] 2: Destroy all humans.",
                ""
        );
        execute("quit");
    }
    @Test(timeout = 1000) public void
    checkTaskTest() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");
        execute("add task training Primitive Obsession");
        execute("add task training Outside-In TDD");
        execute("add task training Interaction-Driven Design");

        execute("check 1");
        execute("check 3");
        execute("check 4");

        execute("show");
        readLines(
                "training",
                "    [x] 1: Four Elements of Simple Design",
                "    [ ] 2: SOLID",
                "    [x] 3: Coupling and Cohesion",
                "    [x] 4: Primitive Obsession",
                "    [ ] 5: Outside-In TDD",
                "    [ ] 6: Interaction-Driven Design",
                ""
        );

        execute("quit");
    }

    @Test(timeout = 1000) public void
    uncheckTaskTest() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");
        execute("add task training Primitive Obsession");
        execute("add task training Outside-In TDD");
        execute("add task training Interaction-Driven Design");

        execute("check 1");
        execute("check 3");
        execute("check 4");

        execute("show");
        readLines(
                "training",
                "    [x] 1: Four Elements of Simple Design",
                "    [ ] 2: SOLID",
                "    [x] 3: Coupling and Cohesion",
                "    [x] 4: Primitive Obsession",
                "    [ ] 5: Outside-In TDD",
                "    [ ] 6: Interaction-Driven Design",
                ""
        );
        execute("uncheck 3");
        execute("uncheck 4");
        execute("show");
        readLines(
                "training",
                "    [x] 1: Four Elements of Simple Design",
                "    [ ] 2: SOLID",
                "    [ ] 3: Coupling and Cohesion",
                "    [ ] 4: Primitive Obsession",
                "    [ ] 5: Outside-In TDD",
                "    [ ] 6: Interaction-Driven Design",
                ""
        );
        execute("quit");
    }
    @Test(timeout = 1000) public void
    helpTest() throws IOException {
        execute("help");
        readLines("Commands:",
                "  show",
                "  add project <project name>",
                "  add task <project name> <task description>",
                "  check <task ID>",
                "  uncheck <task ID>",
                ""
        );
        execute("quit");
    }
    @Test(timeout = 1000) public void
    projectNotFoundTest() throws IOException {
        execute("add task training task qui ne marche pas");
        readLines("Could not find a project with the name \"training\"."
        );
        execute("quit");
    }

    @Test(timeout = 1000) public void
    commandNotFoundTest() throws IOException {
        execute("helpss");
        readLines("I don't know what the command \"helpss\" is."
        );
        execute("quit");
    }
    @Test(timeout = 1000) public void
    idNotFoundTest() throws IOException {
        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");

        execute("check 9");
        readLines("Could not find a task with an ID of 9."
        );


        execute("quit");
    }
    @Test(timeout = 1000) public void
    createProjectWithoutNameTest() throws IOException {
        execute("add project");
        readLines("Please enter a project name");
        execute("add project ");
        readLines("Please enter a project name");
        execute("quit");
    }
    @Test(timeout = 1000) public void
    createTaskWithoutProjectNameTest() throws IOException {
        execute("add task");
        readLines("Please enter the project name and a task name");
        execute("add task ");
        readLines("Please enter the project name and a task name");
        execute("quit");
    }
    @Test(timeout = 1000) public void
    createTaskWithoutTaskNameTest() throws IOException {
        execute("add project projet");
        execute("add task projet");
        readLines("Please enter a task name");
        execute("add task projet ");
        readLines("Please enter a task name");
        execute("quit");
    }

    private void execute(String command) throws IOException {
        read(PROMPT);
        write(command);
    }

    private void read(String expectedOutput) throws IOException {
        int length = expectedOutput.length();
        char[] buffer = new char[length];
        outReader.read(buffer, 0, length);
        assertThat(String.valueOf(buffer), is(expectedOutput));
    }

    private void readLines(String... expectedOutput) throws IOException {
        for (String line : expectedOutput) {
            read(line + lineSeparator());
        }
    }

    private void write(String input) {
        inWriter.println(input);
    }

    private boolean stillRunning() {
        return applicationThread != null && applicationThread.isAlive();
    }
}
