@ECHO OFF
TITLE LV

SET SB_HOME_LOCAL=C:\TIBCO\sb-cep\7.6
SET WORKSPACE_HOME=D:\Work\EPBU\Demo\Dynamic Deployment\containerisation.git\src\lv\sample

:: Change folder
cd /D %SB_HOME_LOCAL%
:: Set environment variables for SB/LV
call setenv.bat 64

:: change folder for LV Server
cd /D %SB_HOME_LOCAL%\liveview\tools
:: Start the server.
call lv-server.cmd run "%WORKSPACE_HOME%"
pause
