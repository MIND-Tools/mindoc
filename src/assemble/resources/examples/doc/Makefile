
ifdef MIND_ROOT
  # If MIND_ROOT contains '\', replace them by '/'
  MINDOC = $(subst \,/, $(MIND_ROOT)/bin/mindoc)
  LINKLINT = $(subst \,/, $(MIND_ROOT)/bin/linklint-2.3.5)
else 
  MINDOC = mindoc
  LINKLINT = linklint-2.3.5
endif
#MIND_ROOT=..

ifndef REPORT_ROOT
  REPORT_ROOT = linklint-report
endif

# GJA: unable to run linklint on windows ...
#all: doc check-doc 
all: doc

doc:
	$(MINDOC) --overview=template_test/overview.html --doctitle="Mindoc Example Application" .

check-doc:
	@if [ -x $(LINKLINT) ]; then echo "Using linklint: $(LINKLINT)" && $(LINKLINT) -error -warn -doc $(REPORT_ROOT) -root ./target/doc/ /@; fi 

clean_all:
	-rm */*~ -f
	-rm target/* -rf
