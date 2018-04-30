/*
 *  Copyright 2018 Valtech GmbH
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *  
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.valtech.aecu.core.jmx;

import javax.management.NotCompliantMBeanException;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.adobe.granite.jmx.annotation.AnnotatedStandardMBean;

import de.valtech.aecu.service.AecuService;

@Component(service = {AecuServiceMBean.class}, immediate = true, property = {
    "jmx.objectname=de.valtech:type=AECU",
    "pattern=/.*"
})
public class AecuServiceMBeanImpl extends AnnotatedStandardMBean implements AecuServiceMBean {
    
    @Reference
    AecuService aecuService;

    /**
     * Constructor
     * 
     * @throws NotCompliantMBeanException
     */
    public AecuServiceMBeanImpl() throws NotCompliantMBeanException {
        super(AecuServiceMBean.class);
    }

    @Override
    public String getVersion() {
        return aecuService.getVersion();
    }

}
