package pso.decision_engine.service;

public interface JwtService {

	public String generateJwt(String userId);
	
	/**
	 * 
	 * @param jwt
	 * @return user or null
	 */
	public String verifyJwt(String jwt);
	
	public String verifySqeUMJwt(String jwt);

	public String getJwtPayload(String jwt);

}
