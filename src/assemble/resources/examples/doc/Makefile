# If the MIND_ROOT variable is defined, look for Mind binaries in this directory
# otherwise, expect that Mind binaries are in the PATH.
ifdef MIND_ROOT
  # If MIND_ROOT contains '\', replace them by '/' (for Windows)
  MINDC = $(subst \,/, $(MIND_ROOT)/bin/mindc)
  MINDUNIT = $(subst \,/, $(MIND_ROOT)/bin/mindunit)
  MINDOC = $(subst \,/, $(MIND_ROOT)/bin/mindoc)
  LINKLINT = $(subst \,/, $(MIND_ROOT)/bin/linklint-2.3.5)
else 
  MINDC = mindc
  MINDUNIT = mindunit
  MINDOC = mindoc
  LINKLINT = linklint-2.3.5
endif

# Non-MindEd users should invoke this Makefile using "make CONFIGURATION=<configName> all" to run the build,
# where <configName> matches the desired <configName>.properties file.

# Set CONFIGURATION to Default if not set
CONFIGURATION ?= Default
include $(CONFIGURATION).properties

confOutputDirVar=target/$(CONFIGURATION)

binOutputDir=$(confOutputDirVar)/binaries
depsOutputDir=$(confOutputDirVar)/dependencies
docOutputDir=$(confOutputDirVar)/doc
graphOutputDir=$(confOutputDirVar)/graph
testBinOutputDir=$(confOutputDirVar)/test-binaries
testDocOutputDir=$(confOutputDirVar)/test-doc

# Strings and comments management - Skip to "all:" for the real build.

#Platform Separator handling
PATH_SEPARATOR =:
ifneq (,$(findstring Windows, $(OS))) # Handle Windows-specific separators (':' otherwise)
    PATH_SEPARATOR =;
endif

sourcePathArg=
ifdef sourcePath
	ifneq ($(sourcePath), "")
		sourcePathArg=--src-path="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(sourcePath):$(depsOutputDir)))"
	endif
endif

testSourcePathArg=
ifdef testSourcePath
	ifneq ($(testSourcePath), "")
		testSourcePathArg="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(testSourcePath)))"
	endif
endif

includePathArg=
ifdef includePath
	ifneq ($(includePath), "")
		includePathArg=--inc-path="$(subst :,$(PATH_SEPARATOR),$(subst ::,:,$(includePath)))"
	endif
endif

compilerCommandArg=
ifdef compilerCommand
	ifneq ($(compilerCommand), "")
		compilerCommandArg=--compiler-command="$(compilerCommand)"
	endif
endif

linkerCommandArg=
ifdef linkerCommand
	ifneq ($(linkerCommand), "")
		linkerCommandArg=--linker-command="$(linkerCommand)"
	endif
endif

assemblerCommandArg=
ifdef assemblerCommand
	ifneq ($(assemblerCommand), "")
		assemblerCommandArg=--assembler-command="$(assemblerCommand)"
	endif
endif

asFlagsArg=
ifdef asFlags
	ifneq ($(asFlags), "")
		asFlagsArg=--as-flags="$(asFlags)"
	endif
endif

cppFlagsArg=
ifdef cppFlags
	ifneq ($(cppFlags), "")
		cppFlagsArg=--cpp-flags="$(cppFlags)"
	endif
endif

cFlagsArg=
ifdef cFlags
	ifneq ($(cFlags), "")
		cFlagsArg=--c-flags="$(cFlags)"
	endif
endif

ldFlagsArg=
ifdef ldFlags
	ifneq ($(ldFlags), "")
		ldFlagsArg=--ld-flags="$(ldFlags)"
	endif
endif

extraOptionsArg=
ifdef extraOptions
	ifneq ($(extraOptions), "")
		extraOptionsArg=$(extraOptions)
	endif
endif

docExtraOptionsArg=
ifdef docExtraOptions
	ifneq ($(docExtraOptions), "")
		docExtraOptionsArg=$(docExtraOptions)
	endif
endif

testExtraOptionsArg=
ifdef testExtraOptions
	ifneq ($(testExtraOptions), "")
		testExtraOptionsArg=$(testExtraOptions)
	endif
endif


# The real build command.

all: dependencies compile graph doc test  

clean: clean_dependencies clean_compile clean_graph clean_doc clean_test

dependencies: build_dependencies
# Add 'cp' commands here to copy your project dependencies files to depsOutputDir

compile: build 
ifdef targetComponent
	@if test ! -x $(MINDC) && ! (which $(MINDC) 2>&1) > /dev/null ; \
		then echo ' Mindc not found! Set MIND_ROOT environment variable or set 'Mindc location' in Preference page of Eclipse' && exit 1 ; \
		else $(MINDC) $(sourcePathArg) $(includePathArg) $(compilerCommandArg) $(linkerCommandArg) $(assemblerCommandArg) $(asFlagsArg) $(cppFlagsArg) $(cFlagsArg) $(ldFlagsArg) $(extraOptionsArg) -o=$(binOutputDir) $(targetComponent):$(binaryName) ;\
	fi
else
	@echo "Compile: No application defined - MindEd users: Please configure your project target with the 'Mind Path' -> (right click) 'New application' menu."
endif

graph: build_graph
ifdef targetComponent
	@if test ! -x $(MINDC) && ! (which $(MINDC) 2>&1) > /dev/null ; \
		then echo ' Mindc not found! Set MIND_ROOT environment variable or set 'Mindc location' in Preference page of Eclipse' && exit 1 ; \
		else $(MINDC) --check-adl $(sourcePathArg) $(includePathArg) -o=$(graphOutputDir) 'dot.DumpComponentDot<'$(targetComponent)'>' ;\
	fi
else
	@echo "Graph: No application defined - MindEd users: Please configure your project target with the 'Mind Path' -> (right click) 'New application' menu."
endif

doc: build_doc
	@if test ! -x $(MINDOC) && ! (which $(MINDOC) 2>&1) > /dev/null ; \
		then echo ' Mindoc not found! Set MIND_ROOT environment variable or set 'Mindc location' in Preference page of Eclipse' && exit 1 ; \
		else $(MINDOC) $(sourcePathArg) $(docExtraOptionsArg) -o=$(docOutputDir);\
	fi

# Linklint can be used to check generated documentation (Linklint requires Perl)
check_doc: 
	@if test ! -x $(LINKLINT) && ! (which $(LINKLINT) 2>&1) > /dev/null ; \
		then echo ' Linklint not found! Set MIND_ROOT environment variable or set 'Mindc location' in Preference page of Eclipse' && exit 1 ; \
		else $(LINKLINT) -error -warn -root $(docOutputDir) /@ ;\
	fi

# here we reuse the same flags as the common build ones
test: build_test
ifdef testSourcePath
	@if test ! -x $(MINDUNIT) && ! (which $(MINDUNIT) 2>&1) > /dev/null ; \
		then echo ' Mindunit not found! Set MIND_ROOT environment variable or set 'Mindc location' in Preference page of Eclipse' && exit 1 ; \
		else $(MINDUNIT) $(sourcePathArg) $(includePathArg) $(compilerCommandArg) $(linkerCommandArg) $(assemblerCommandArg) $(asFlagsArg) $(cppFlagsArg) $(cFlagsArg) $(ldFlagsArg) $(extraOptionsArg) $(testExtraOptionsArg) -o=$(testBinOutputDir) $(testSourcePathArg);\
	fi
else
	@echo "No test folder defined: Please configure the 'testSourcePath' variable in your $(CONFIGURATION).properties file."
endif

# Output folder handling.

build:
	@mkdir -p $(binOutputDir)

build_dependencies:
	@mkdir -p $(depsOutputDir)

build_doc:
	@mkdir -p $(docOutputDir)

build_test:
	@mkdir -p $(testBinOutputDir)

build_graph:
	@mkdir -p $(graphOutputDir)

clean_compile:
	@rm -Rf $(binOutputDir); \
	mkdir -p $(binOutputDir)

clean_dependencies:
	@rm -Rf $(depsOutputDir); \
	mkdir -p $(depsOutputDir)

clean_doc:
	@rm -Rf $(docOutputDir); \
	mkdir -p $(docOutputDir)

clean_test:
	@rm -Rf $(testBinOutputDir); \
	mkdir -p $(testBinOutputDir)

clean_graph:
	@rm -Rf $(graphOutputDir); \
	mkdir -p $(graphOutputDir)

.PHONY: all dependencies compile graph doc test clean clean_dependencies clean_compile clean_doc clean_test clean_graph build build_dependencies build_doc build_test build_graph

