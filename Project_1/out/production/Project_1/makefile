all : BufMgrTester.java
	javac *.java
	java BufMgrTester `cat param.txt` < input.txt
	find ./*[0-9].txt -exec echo -n "{}   " \; -exec cat {} \;  -exec echo \;

clean: BufMgrTester.class
	rm *.class
	rm *[0-9].txt
