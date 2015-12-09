/**
 * CreditScoreService_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package creditBureau;

public class CreditScoreService_ServiceLocator extends org.apache.axis.client.Service implements creditBureau.CreditScoreService_Service {

    public CreditScoreService_ServiceLocator() {
    }


    public CreditScoreService_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public CreditScoreService_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for CreditScoreServicePort
    private java.lang.String CreditScoreServicePort_address = "http://datdb.cphbusiness.dk:8080/CreditBureau/CreditScoreService";

    public java.lang.String getCreditScoreServicePortAddress() {
        return CreditScoreServicePort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String CreditScoreServicePortWSDDServiceName = "CreditScoreServicePort";

    public java.lang.String getCreditScoreServicePortWSDDServiceName() {
        return CreditScoreServicePortWSDDServiceName;
    }

    public void setCreditScoreServicePortWSDDServiceName(java.lang.String name) {
        CreditScoreServicePortWSDDServiceName = name;
    }

    public creditBureau.CreditScoreService_PortType getCreditScoreServicePort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(CreditScoreServicePort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getCreditScoreServicePort(endpoint);
    }

    public creditBureau.CreditScoreService_PortType getCreditScoreServicePort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            creditBureau.CreditScoreServicePortBindingStub _stub = new creditBureau.CreditScoreServicePortBindingStub(portAddress, this);
            _stub.setPortName(getCreditScoreServicePortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setCreditScoreServicePortEndpointAddress(java.lang.String address) {
        CreditScoreServicePort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (creditBureau.CreditScoreService_PortType.class.isAssignableFrom(serviceEndpointInterface)) {
                creditBureau.CreditScoreServicePortBindingStub _stub = new creditBureau.CreditScoreServicePortBindingStub(new java.net.URL(CreditScoreServicePort_address), this);
                _stub.setPortName(getCreditScoreServicePortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("CreditScoreServicePort".equals(inputPortName)) {
            return getCreditScoreServicePort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://service.web.credit.service.bank.org/", "CreditScoreService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://service.web.credit.service.bank.org/", "CreditScoreServicePort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("CreditScoreServicePort".equals(portName)) {
            setCreditScoreServicePortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
