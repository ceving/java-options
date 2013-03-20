VERSION := $(shell cat VERSION)
SOURCE := szi/options/Options.java
BINJAR := szi-options-$(VERSION).jar
DOCJAR := szi-options-$(VERSION)-javadoc.jar

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

example: example.sh
	$(JAVAC) -cp $(BINJAR) example.java

example.sh: example.sh.in VERSION
	sed "s/VERSION/$(VERSION)/" < $< > $@

run:
	@echo "Example: java -cp .:$(BINJAR) example"

tag:
	git tag -a v$(VERSION) -m "Version $(VERSION)"

lines:
	find $$(dirname $(SOURCE)) -name \*.java | xargs cat | grep -cv '^\s*$$'

$(BINJAR): build
$(DOCJAR): doc

.PHONY: all build doc clean example run tag
