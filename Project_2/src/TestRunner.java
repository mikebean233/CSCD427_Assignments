import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;

public class TestRunner implements Runnable{
	public static void main(String[] args) {
		(new TestRunner()).run();
	}

	public void run(){
		//Result result = JUnitCore.runClasses(BTNodeTest.class, BPlusTreeTest.class);
		JUnitCore runner = new JUnitCore();
		runner.addListener(new Listener());
		Result result = runner.run(BTNode.class, BPlusTreeTest.class);

		System.out.println(result.wasSuccessful());
	}

	private class Listener extends RunListener {
		public Listener(){
		}

		@Override
		public void testFinished(Description description){
			System.out.println(description);
		}

		@Override
		public void testFailure(Failure failure){
			System.err.println(failure.getMessage());
		}
	}
}
