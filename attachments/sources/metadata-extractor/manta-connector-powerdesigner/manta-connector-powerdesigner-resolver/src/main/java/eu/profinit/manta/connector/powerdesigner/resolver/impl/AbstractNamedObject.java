package eu.profinit.manta.connector.powerdesigner.resolver.impl;

import eu.profinit.manta.connector.powerdesigner.model.NamedObject;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Base implementation used by objects that have name and can be further described by metadata.
 *
 * @author ddrobny
 */
public abstract class AbstractNamedObject implements NamedObject {
    /** Name of the object. */
    private String name;
    /** Code of the object. */
    private String code;
    /** Comment of the object. */
    private String comment;
    /** Annotation of the object. */
    private String annotation;
    /** Definition of the object. */
    private String definition;
    /** Globally unique ObjectID. */
    private String objectId;
    /** Set of keywords describing the object. */
    private Set<String> keywords = new HashSet<>();
    /** Global IDs (ObjectIDs) of external objects from which the object was generated. */
    private Set<String> history = new HashSet<>();
    /** Set of attributes that are interesting for a user to be shown if the object is visualized. */
    private Map<String, String> attributes = new HashMap<>();

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = StringUtils.defaultIfBlank(comment, null);

        attributes.put("COMMENT", this.comment);
    }

    @Override
    public String getAnnotation() {
        return annotation;
    }

    public void setAnnotation(String annotation) {
        this.annotation = StringUtils.defaultIfBlank(annotation, null);

        attributes.put("ANNOTATION", this.annotation);
    }

    @Override
    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = StringUtils.defaultIfBlank(definition, null);

        attributes.put("DEFINITION", this.definition);
    }

    @Override
    public Set<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(Set<String> keywords) {
        this.keywords = keywords;

        // Concatenate the keywords with comma as the delimiter
        String keywordString = keywords.stream().collect(Collectors.joining(", "));
        keywordString = StringUtils.defaultIfBlank(keywordString, null);

        attributes.put("KEYWORDS", keywordString);
    }

    @Override
    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Set<String> getHistory() {
        return history;
    }

    public void setHistory(Set<String> history) {
        this.history = history;
    }

    @Override
    public Map<String, String> getMetadata() {
        return attributes;
    }
}
