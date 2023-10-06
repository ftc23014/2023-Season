package org.firstinspires.ftc.lib.gpu;

import org.firstinspires.ftc.lib.systems.Subsystem;
import org.firstinspires.ftc.teamcode.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Shader {
    public static String[] getShader(int resourceNumber) throws IOException {
        //the shader is located in the resources folder, in a shader subfolder
        InputStream is = Subsystem.getHardwareMap().appContext.getResources().openRawResource(resourceNumber);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        String[] lines = br.lines().toArray(String[]::new);

        is.close();
        br.close();

        return lines;
    }
}
