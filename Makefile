SOURCE := szi/options/Options.java
BINJAR := szi-options.jar
DOCJAR := szi-options-javadoc.jar

JAVAC := javac -Xlint -g

all: $(BINJAR)

build: $(SOURCE)
	rm -rf $@
	mkdir $@
	$(JAVAC) -d $@ $^
	jar cf $(BINJAR) -C $@ .

doc: $(SOURCE)
	rm -rf $@
	mkdir $@
	javadoc -d $@ $<
	jar cf $(DOCJAR) -C $@ .

clean:
	rm -rf *.class build doc *.jar

example:
	$(JAVAC) -cp $(BINJAR) example.java

run:
	@echo "Example: java -cp .:$(BINJAR) example"

$(BINJAR): build
$(DOCJAR): doc

.PHONY: all build doc clean example run
