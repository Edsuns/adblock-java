package io.github.edsuns.adblock.util.bucket;

/**
 * Created by Edsuns@qq.com on 2021/10/10.
 */
public interface ExtraDataMatcher {
    /**
     * Match {@link FingerprintBucket.ExtraData} with {@link SubstringBucket.ExtraData}.
     *
     * @param fExtraData {@link FingerprintBucket.ExtraData}
     * @param sExtraData {@link SubstringBucket.ExtraData}
     * @return true if matched
     */
    boolean matches(FingerprintBucket.ExtraData fExtraData, SubstringBucket.ExtraData sExtraData);
}
