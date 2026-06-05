package com.seekweb4.chat.core.mapper.adapters;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@XmlType(name = "MapConvertor")  
@XmlAccessorType(XmlAccessType.FIELD)  
public class MapConvertor {
	
    private List<MapEntry> entries = new ArrayList<MapEntry>();  
  
    public void addEntry(MapEntry entry) {  
        entries.add(entry);  
    }  
  
    public List<MapEntry> getEntries() {  
        return entries;  
    }  
      
    public static class MapEntry {  
  
        private String key;  
  
        private Object value;  
          
        public MapEntry() {  
            super();  
        }  
  
        public MapEntry(Map.Entry<String, Object> entry) {  
            super();  
            this.key = entry.getKey();  
            this.value = entry.getValue();  
        }  
  
        public MapEntry(String key, Object value) {  
            super();  
            this.key = key;  
            this.value = value;  
        }  
  
        public String getKey() {  
            return key;  
        }  
  
        public void setKey(String key) {  
            this.key = key;  
        }  
  
        public Object getValue() {  
            return value;  
        }  
  
        public void setValue(Object value) {  
            this.value = value;  
        }  
    }  
}  