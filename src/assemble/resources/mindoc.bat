@REM  This file is part of "Mind Compiler" is free software: you can redistribute 
@REM  it and/or modify it under the terms of the GNU Lesser General Public License 
@REM  as published by the Free Software Foundation, either version 3 of the 
@REM  License, or (at your option) any later version.
@REM 
@REM  This program is distributed in the hope that it will be useful, but WITHOUT 
@REM  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
@REM  FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public License for more
@REM  details.
@REM 
@REM  You should have received a copy of the GNU Lesser General Public License
@REM  along with this program.  If not, see <http://www.gnu.org/licenses/>.
@REM 
@REM  Contact: mind@ow2.org
@REM 
@REM  Authors: Stephane Seyvoz (sseyvoz@assystem.com)
@REM  Contributors: 
@REM
@REM  Note: Inspired by the mindc.bat script from Edine Coly
@REM -----------------------------------------------------------------------------
@REM Mind Doc batch script ${project.version}
@REM
@REM Required ENV vars:
@REM ------------------
@REM   JAVA_HOME - location of a JDK home dir
@REM
@REM Optional ENV vars
@REM -----------------
@REM   MINDOC_HOME - location of mind's installed home dir
@REM   MINDOC_OPTS - parameters passed to the Java VM running the mind compiler
@REM   See documentation for more detail on logging system.

@echo off

@REM ==== CHECK JAVA_HOME ===
if not "%JAVA_HOME%" == "" goto OkJHome
echo.
echo ERROR: JAVA_HOME not found in your environment.
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHome
@REM ==== CHECK JAVA_HOME_EXE ===
if exist "%JAVA_HOME%\bin\java.exe" goto OkJHomeExe

echo.
echo ERROR: JAVA_HOME is set to an invalid directory.
echo JAVA_HOME = "%JAVA_HOME%"
echo Please set the JAVA_HOME variable in your environment to match the
echo location of your Java installation
echo.
goto error

:OkJHomeExe
@REM ==== CHECK MINDOC_HOME ===
@REM use the batch path to determine MINDOC_HOME if not defined.
pushd %~dp0..\
set MINDOC_ROOT=%cd%
popd

if "%MINDOC_HOME%" == "" set MINDOC_HOME=%MINDOC_ROOT%

@REM MINDOC_HOME defined and different from batch path, use it but warn the user
if /i "%MINDOC_HOME%" == "%MINDOC_ROOT%" goto endInit
echo.
echo WARNING: Using environment variable MINDOC_HOME which is different from mindoc.bat location
echo MINDOC_HOME         = %MINDOC_HOME% 
echo mindoc.bat location = %MINDOC_ROOT%
echo.

:endInit

setlocal
set MIND_CMD_LINE_ARGS=%*
set MINDOC_LIB=%MINDOC_HOME%/lib
set MINDOC_EXT=%MINDOC_HOME%/ext
set LAUNCHER=org.ow2.mind.Launcher
set MIND_JAVA_EXE="%JAVA_HOME%\bin\java.exe"
if not "%MINDOC_CLASSPATH%" == "" set MINDOC_CLASSPATH=%MINDOC_CLASSPATH%;

for /r "%MINDOC_LIB%\" %%i in (*.jar) do (
    set VarTmp=%%~fnxi;& call :concat
    )
for /r "%MINDOC_EXT%\" %%i in (*.jar) do (
    set VarTmp=%%~fnxi;& call :concat
    )

goto :runMind
:concat
set MINDOC_CLASSPATH=%MINDOC_CLASSPATH%%VarTmp%
goto :eof

:runMind
%MIND_JAVA_EXE% -classpath %MINDOC_CLASSPATH% %MINDOC_OPTS% %LAUNCHER% %MIND_CMD_LINE_ARGS%


:error
@echo off
if "%OS%"=="Windows_NT" @endlocal
if "%OS%"=="WINNT" @endlocal
(set ERROR_CODE=1)
