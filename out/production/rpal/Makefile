JC = javac
MAIN_CLASS = rpal20
SRCS = $(wildcard *.java)
OBJS = $(SRCS:.java=.class)

.PHONY: all clean

all: $(MAIN_CLASS)

$(MAIN_CLASS): $(OBJS)
	@echo "Main-Class: $(MAIN_CLASS)" > Manifest.txt
	@jar cvfm $(MAIN_CLASS).jar Manifest.txt $(OBJS)
	@rm Manifest.txt

%.class: %.java
	$(JC) $<

clean:
	@rm -f $(OBJS) $(MAIN_CLASS).jar
