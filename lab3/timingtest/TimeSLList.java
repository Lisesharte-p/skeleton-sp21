package timingtest;
import edu.princeton.cs.algs4.Stopwatch;
import org.apache.commons.math3.analysis.solvers.AllowedSolution;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer>Ns=new AList<>();
        AList<Double>times=new AList<>();
        AList<Integer>ops=new AList<>();
        int x=1;
        int OPS=10000;
        Ns.addLast(1000);
        Ns.addLast(2000);
        Ns.addLast(4000);
        Ns.addLast(8000);
        Ns.addLast(16000);
        Ns.addLast(32000);
        Ns.addLast(64000);
        Ns.addLast(128000);
        for(int i=0;i<8;i++){
            SLList<Integer>ds=new SLList<>();

            for(int j=Ns.get(i);j>0;j--){
                ds.addLast(x);
            }
            Stopwatch sw=new Stopwatch();
            for(int j=0;j<OPS;j++){
                Integer last = ds.getLast();
            }
            ops.addLast(OPS);
            Double time_elapsed=sw.elapsedTime();
            times.addLast(time_elapsed);

        }
        printTimingTable(Ns,times,ops);
    }

}
