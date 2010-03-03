MINDOC_ROOT=..
LINKLINT=$(MINDOC_ROOT)/bin/linklint-2.3.5

all: doc check-doc

REPORT_ROOT=linklint-report

doc:
	$(MINDOC_ROOT)/bin/mindoc -v -overview template_test/overview.html -doctitle "Mindoc Example Application" .

check-doc:
	@if [ -x $(LINKLINT) ]; then echo "Using linklint: $(LINKLINT)" && $(LINKLINT) -error -warn -doc $(REPORT_ROOT) -root ./target/doc/ /@; fi 

clean:
	-rm */*~ -f
	-rm target/* -rf
