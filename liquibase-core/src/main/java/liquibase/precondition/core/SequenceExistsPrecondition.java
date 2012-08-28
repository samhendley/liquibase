package liquibase.precondition.core;

import liquibase.changelog.DatabaseChangeLog;
import liquibase.changelog.ChangeSet;
import liquibase.database.Database;
import liquibase.database.structure.Schema;
import liquibase.database.structure.Sequence;
import liquibase.diff.DiffControl;
import liquibase.exception.*;
import liquibase.precondition.Precondition;
import liquibase.snapshot.DatabaseSnapshot;
import liquibase.snapshot.DatabaseSnapshotGeneratorFactory;

public class SequenceExistsPrecondition implements Precondition {
    private String catalogName;
    private String schemaName;
    private String sequenceName;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getSchemaName() {
        return schemaName;
    }

    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    public String getSequenceName() {
        return sequenceName;
    }

    public void setSequenceName(String sequenceName) {
        this.sequenceName = sequenceName;
    }

        public Warnings warn(Database database) {
        return new Warnings();
    }

    public ValidationErrors validate(Database database) {
        return new ValidationErrors();
    }

    public void check(Database database, DatabaseChangeLog changeLog, ChangeSet changeSet) throws PreconditionFailedException, PreconditionErrorException {
        DatabaseSnapshot snapshot;
        Schema schema = new Schema(getCatalogName(), getSchemaName());
        try {
            snapshot = DatabaseSnapshotGeneratorFactory.getInstance().createSnapshot(database, new DiffControl(schema, Sequence.class));
        } catch (DatabaseException e) {
            throw new PreconditionErrorException(e, changeLog, this);
        }
        if (snapshot.getDatabaseObject(schema, getSequenceName(), Sequence.class) == null) {
            throw new PreconditionFailedException("Sequence "+database.escapeSequenceName(getCatalogName(), getSchemaName(), getSequenceName())+" does not exist", changeLog, this);
        }
    }

    public String getName() {
        return "sequenceExists";
    }
}