@setlocal enableextensions
@cd /d "%~dp0"
SET _JAVA_OPTIONS=
SET PZ_CLASSPATH=istack-commons-runtime.jar;jassimp.jar;javacord-2.0.17-shaded.jar;javax.activation-api.jar;jaxb-api.jar;jaxb-runtime.jar;lwjgl.jar;lwjgl-natives-windows.jar;lwjgl-glfw.jar;lwjgl-glfw-natives-windows.jar;lwjgl-jemalloc.jar;lwjgl-jemalloc-natives-windows.jar;lwjgl-opengl.jar;lwjgl-opengl-natives-windows.jar;lwjgl_util.jar;sqlite-jdbc-3.27.2.1.jar;trove-3.0.3.jar;uncommons-maths-1.2.3.jar;./
".\jre64\bin\java.exe" -XX:+UseZGC -XX:-CreateCoredumpOnCrash -XX:-OmitStackTraceInFastThrow -Xms%memory% -Xmx%memory% -XX:+UseZGC -Djava.library.path=./natives/;./natives/win64/;./ -cp %PZ_CLASSPATH% zombie.network.GameServer