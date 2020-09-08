package pw.cdmi.file.engine.filesystem.manage;

import pw.cdmi.file.engine.filesystem.model.FSEndpoint;
import pw.cdmi.file.engine.object.domain.FileObject;
import pw.cdmi.file.engine.object.domain.MultipartFileObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.commons.collections.CollectionUtils;

public class SelectBestStorageAlgorithm extends SelectAlgorithm {
	private static final Random RANDOM_SELECTOR = new Random(System.currentTimeMillis());

	public FSEndpoint select(List<FSEndpoint> allWriteAbleEndpoints, Object condition) {
		if (allWriteAbleEndpoints.size() == 1) {
			return (FSEndpoint) allWriteAbleEndpoints.get(0);
		}
		List<FSEndpoint> bestStorages = new ArrayList<FSEndpoint>(allWriteAbleEndpoints.size());
		if ((condition instanceof MultipartFileObject)) {
			for (FSEndpoint endpoint : allWriteAbleEndpoints) {
				if (endpoint.getMultipartFirst().booleanValue()) {
					bestStorages.add(endpoint);
				}
			}
		} else {
			FileObject fileObject = (FileObject) condition;
			long objectLength = fileObject.getObjectLength();
			long bestEndRangetemp = 0L;
			for (FSEndpoint endpoint : allWriteAbleEndpoints) {
				bestEndRangetemp = endpoint.getBestEndRange().longValue();
				if (bestEndRangetemp == -1L) {
					bestEndRangetemp = 9223372036854775807L;
				}
				if ((objectLength > endpoint.getBestStartRange().longValue()) && (objectLength <= bestEndRangetemp)) {
					bestStorages.add(endpoint);
				}
			}
		}
		if (CollectionUtils.isEmpty(bestStorages)) {
			return (FSEndpoint) allWriteAbleEndpoints.get(RANDOM_SELECTOR.nextInt(allWriteAbleEndpoints.size()));
		}

		return (FSEndpoint) bestStorages.get(RANDOM_SELECTOR.nextInt(bestStorages.size()));
	}
}