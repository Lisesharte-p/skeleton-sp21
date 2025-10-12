package gh2;
import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GH {
    public static final double CONCORD_A=440.0;
    public static String keyboard="q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";
    public static GuitarString[] gs=new GuitarString[37];
    public static void main(String[] args){
        for(int i=0;i<37;i++){
            GuitarString newgs=new GuitarString(CONCORD_A*Math.pow(2, (double) (i - 24) /12.0));
            gs[i]=newgs;
        }


        while (true){

            if(StdDraw.hasNextKeyTyped()){
                char key=StdDraw.nextKeyTyped();
                if(keyboard.indexOf(key)!=-1){
                    gs[keyboard.indexOf(key)].pluck();

                }

            }
            double sample=0.0;


            for(GuitarString x:gs){
                sample+=x.sample();
                x.tic();
            }
            StdAudio.play(sample);
        }

    }
}
