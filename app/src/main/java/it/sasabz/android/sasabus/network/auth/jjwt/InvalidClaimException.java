package it.sasabz.android.sasabus.network.auth.jjwt;

/**
 * Exception indicating a parsed claim is invalid in some way.  Subclasses reflect the specific
 * reason the claim is invalid.
 *
 * @see IncorrectClaimException
 * @see MissingClaimException
 * @since 0.6
 */
public class InvalidClaimException extends ClaimJwtException {

    private static final long serialVersionUID = 7125550855747734142L;

    private String claimName;
    private Object claimValue;

    InvalidClaimException(Header header, Claims claims, String message) {
        super(header, claims, message);
    }

    public void setClaimName(String claimName) {
        this.claimName = claimName;
    }

    public void setClaimValue(Object claimValue) {
        this.claimValue = claimValue;
    }
}
