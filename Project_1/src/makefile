all : BufMgrTester.java
	javac *.java

clean: BufMgrTester.class
	rm *.class
	rm *[0-9].txt

run: BufMgrTester.java
	java BufMgrTester `cat param.txt` < input.txt
	find ./*[0-9].txt -exec echo -n "{}   " \; -exec cat {} \;  -exec echo \;

