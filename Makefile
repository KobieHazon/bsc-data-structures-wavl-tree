JAVAC ?= javac
JAVA ?= java
BUILD_DIR := build
SOURCES := $(shell find src tests -name '*.java')

.PHONY: compile test clean

compile:
	mkdir -p $(BUILD_DIR)
	$(JAVAC) -encoding UTF-8 -Xlint:all -Werror -d $(BUILD_DIR) $(SOURCES)

test: compile
	$(JAVA) -cp $(BUILD_DIR) RunWavlChecks

clean:
	rm -rf $(BUILD_DIR)
