SOURCE := szi/options/Options.java
BINJAR := szi-options.jar
DOCJAR := szi-options-javadoc.jar

all: $(BINJAR)

build: $(SOURCE)
	rm -rf $@
	mkdir $@
	javac -d $@ $^
	jar cf $(BINJAR) -C $@ .

doc: $(SOURCE)
	rm -rf $@
	mkdir $@
	javadoc -d $@ $<
	jar cf $(DOCJAR) -C $@ .

clean:
	rm -rf *.class build doc *.jar

example:
	javac -cp .:$(BINJAR) example.java

$(BINJAR): build
$(DOCJAR): doc

.PHONY: all build doc clean example
