
ifdef MINDOC_ROOT
  # If MINDOC_ROOT contains '\', replace them by '/'
  MINDOC = $(subst \,/, $(MINDOC_ROOT)/bin/mindoc)
  LINKLINT=$(subst \,/, $(MINDOC_ROOT)/bin/linklint-2.3.5)
else 
  MINDOC = mindoc
  LINKLINT=linklint-2.3.5
endif
#MINDOC_ROOT=..


all: doc check-doc

REPORT_ROOT=linklint-report

doc:
	$(MINDOC) -v --overview=template_test/overview.html --doctitle="Mindoc Example Application" .

check-doc:
	@if [ -x $(LINKLINT) ]; then echo "Using linklint: $(LINKLINT)" && $(LINKLINT) -error -warn -doc $(REPORT_ROOT) -root ./target/doc/ /@; fi 

clean:
	-rm */*~ -f
	-rm target/* -rf
