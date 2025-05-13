package VoyContigo_terminalOnline.service;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class FileStorageService {

    @Autowired
    private GridFSBucket gridFSBucket;

    public String storeFile(MultipartFile file) throws IOException {
        GridFSUploadOptions options = new GridFSUploadOptions();
        ObjectId fileId = gridFSBucket.uploadFromStream(
                file.getOriginalFilename(),
                file.getInputStream(),
                options
        );
        return fileId.toHexString();
    }
}
