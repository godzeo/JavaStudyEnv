//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//public class Evil {
//    String res;
//    public static String exec(String cmd) throws IOException {
//        StringBuilder stringBuilder = new StringBuilder();
//        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec(cmd).getInputStream()));
//        String line;
//        while ((line = bufferedReader.readLine()) != null) {
//            stringBuilder.append(line).append("\n");
//        }
//        return stringBuilder.toString();
//    }
//
//    @Override
//    public String toString() {
//        return res;
//    }
//}
