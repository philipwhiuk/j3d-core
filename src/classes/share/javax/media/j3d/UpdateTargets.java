/*
 * $RCSfile$
 *
 * Copyright 2001-2008 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 *
 * $Revision: 892 $
 * $Date: 2008-02-28 20:18:01 +0000 (Thu, 28 Feb 2008) $
 * $State$
 */

package javax.media.j3d;

class UpdateTargets {

    static int updateSwitchThreads[] = {
	// GEO
        J3dThread.UPDATE_RENDER | J3dThread.UPDATE_RENDERING_ENVIRONMENT |
        J3dThread.UPDATE_GEOMETRY,

	// ENV
        J3dThread.UPDATE_RENDER | J3dThread.UPDATE_RENDERING_ENVIRONMENT,

	// BEH
        J3dThread.UPDATE_BEHAVIOR,

	// SND
        J3dThread.UPDATE_SOUND | J3dThread.SOUND_SCHEDULER,

	// VPF
        J3dThread.UPDATE_RENDER | J3dThread.UPDATE_BEHAVIOR |
        J3dThread.UPDATE_SOUND | J3dThread.SOUND_SCHEDULER,

	// BLN
        J3dThread.UPDATE_RENDER | J3dThread.UPDATE_RENDERING_ENVIRONMENT |
        J3dThread.UPDATE_BEHAVIOR | J3dThread.UPDATE_SOUND,

	// GRP
        0
        };


    UnorderList[] targetList = new UnorderList[Targets.MAX_NODELIST];

    int computeSwitchThreads() {
        int switchThreads = 0;

        for (int i=0; i < Targets.MAX_NODELIST; i++) {
            if (targetList[i] != null) {
                switchThreads |= updateSwitchThreads[i];
            }
        }
        return switchThreads | J3dThread.UPDATE_TRANSFORM;
    }

    void addNode(Object node, int targetType) {
        if(targetList[targetType] == null)
            targetList[targetType] = new UnorderList(1);

        targetList[targetType].add(node);
    }

    
    void addNodeArray(Object[] nodeArr, int targetType) {
	if(targetList[targetType] == null)
	    targetList[targetType] = new UnorderList(1);
	
	targetList[targetType].add(nodeArr);
    }


    void clearNodes() {	
	for(int i=0; i<Targets.MAX_NODELIST; i++) {
	    if (targetList[i] != null) {
	        targetList[i].clear();
	    }
	}
    }

    void addCachedTargets(CachedTargets cachedTargets) {
        for(int i=0; i<Targets.MAX_NODELIST; i++) {
            if (cachedTargets.targetArr[i] != null ) {
                addNodeArray(cachedTargets.targetArr[i], i);
            }
        }
    }

    void dump() {
        for(int i=0; i<Targets.MAX_NODELIST; i++) {
            if (targetList[i] != null) {
                System.err.println("  " + CachedTargets.typeString[i]);
                for(int j=0; j<targetList[i].size(); j++) {
                    System.err.println("  " + targetList[i].get(j));
                }
            }
        }
    }
}
