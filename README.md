# mindoc

## Description

An HTML Documentation generator from ADL and ITF.

# User info

See src/assemble/resources/README_DOC.txt in the current source repository.

Since this tool is now delivered as part of the mind-tools "all-in-one" package, the according README_DOC.txt should be found at the root of the binary package.

## Developer info

The original mindoc was originally be delivered as a standalone tool, including a whole mind-compiler delivery as infrastructure, adding its mindoc jar, launcher script and class, example, and resources.


During its developments, the Mind4SE team observed a few flaws with that approach:
* Users were tired of checking each tool specific version
* Users needed to add multiple entries in their Path (one for each tool)
* Mindoc was decorrelated from the core toolchain (hard dependency to mind-compiler 1.1), embedding deprecated code, not benefiting updates and fixes

It was then decided to plugin-ify the tool, that nowadays MUST be merged with the mind-compiler package so as for its launcher script to work (depdending on mindc's "jar_launcher" and lib/*jar modules).

As of today, the plugin does NOT work in a standalone mode anymore.
To restore such behavior during the build, you simply would have to add an unpack phase for the mind-compiler dependency and to merge content thanks to the src/assemble/bin-release.xml file.
