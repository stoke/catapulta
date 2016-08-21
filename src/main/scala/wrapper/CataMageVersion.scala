/*
 * Copyright 2016 Sandro
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wrapper

import mage.utils.MageVersion

/**
  * Created by Sandro on 19/08/2016.
  */
// This can't inherit MageVersion for serialization issues
case class CataMageVersion(
     major: Int = MageVersion.MAGE_VERSION_MAJOR,
     minor: Int = MageVersion.MAGE_VERSION_MINOR,
     patch: Int = MageVersion.MAGE_VERSION_PATCH,
     minorPatch: String = MageVersion.MAGE_VERSION_MINOR_PATCH,
     info: String = MageVersion.MAGE_VERSION_INFO
)

object CataMageVersion {
    def version(cataVersion: CataMageVersion): MageVersion = new MageVersion(
        cataVersion.major, cataVersion.minor, cataVersion.patch,
        cataVersion.minorPatch, cataVersion.info
    )

    def version: MageVersion = version( CataMageVersion() )
}
