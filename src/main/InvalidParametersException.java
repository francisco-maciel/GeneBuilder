package main;

public class InvalidParametersException extends Exception {

	private static final long serialVersionUID = -1084299832185776152L;

	public  InvalidParametersException() {
        super("Usage: java -jar simulator.jar seq_name seq_length transp_dir transp_per transp_depth gene_dir gene_per repet_dir repet_per ins_rate rem_rate replac_rate [transp_type]");
    }
	
	public  InvalidParametersException(String message) {
        super("Usage: java -jar simulator.jar seq_name seq_length transp_dir transp_per transp_depth gene_dir gene_per repet_dir repet_per ins_rate rem_rate replac_rate [transp_type]\n" + message);
    }
}
