package com.sms.manager.contacts.service;

import com.sms.manager.model.ContactGroup;
import com.sms.manager.model.Contact;
import com.sms.manager.model.TransactionResult;
import com.sms.manager.model.response.ContactGroupLookupResponse;
import com.sms.manager.model.response.ContactLookupResponse;

public interface ContactService {

public TransactionResult addContact(Contact contacts);	

public TransactionResult updateContact(Contact contact);

public TransactionResult deleteContact(Contact contact);	

public TransactionResult addContactGroup(ContactGroup contactList);

TransactionResult updateContactGroup(ContactGroup contactList);

TransactionResult deleteContactGroup(ContactGroup contactList);

ContactGroupLookupResponse contactGroupLookup(String searchValue, String user, int size, int pageNumber);

ContactLookupResponse contactLookup(String searchValue, String user, int size, int pageNumber);	

}
