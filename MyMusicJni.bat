set MY_JAVA_PATH=D:\Java
set JAVA_BIN=%MY_JAVA_PATH%\jdk1.8.0\bin
set AN_PATH=E:\install\dev\adt-bundle-windows-x86-20131030\adt-bundle-windows-x86-20131030\sdk\platforms\android-19

echo %MY_JAVA_PATH%
echo %JAVA_BIN%

%JAVA_BIN%\javah -bootclasspath %AN_PATH%\android.jar -classpath bin/classes -d jni eugen.mymusic.PlayCtrl

pause