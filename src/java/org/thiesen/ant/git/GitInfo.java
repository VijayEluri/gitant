/*
 * $ Id $
 * (c) Copyright 2009 Marcus Thiesen (marcus@thiesen.org)
 *
 *  This file is part of gitant.
 *
 *  gitant is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  gitant is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with gitant.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.thiesen.ant.git;

import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.revwalk.RevTag;


public class GitInfo {

    private static final String SNAPSHOT_POSTFIX = "SNAPSHOT";
    private final String _currentBranch;
    private final String _lastCommit;
    private final String _lastCommitShort;
    private final Date _lastCommitDate;
    private final boolean _workingCopyDirty;
    private final boolean _lastTagDirty;
    private final CustomTag _lastTag;
    private final String _displayString;
    private final String _lastTagAuthorName;
    private final String _lastTagAuthorEmail;


    private GitInfo( final String currentBranch, final String lastCommit, final boolean workingCopyDirty,
            final boolean lastTagDirty, final CustomTag lastTag, final String lastCommitShort, final Date lastCommitDate ) {
        super();
        _currentBranch = currentBranch;
        _lastCommit = lastCommit;
        _lastCommitShort = lastCommitShort;
        _lastCommitDate = lastCommitDate;
        _workingCopyDirty = workingCopyDirty;
        _lastTagDirty = lastTagDirty;
        _lastTag = lastTag;
        if ( lastTag != null ) {
            final RevObject object = lastTag.getObject();
            
            if ( object.getType() == Constants.OBJ_TAG ) {
                final RevTag tag = (RevTag) object;
                final PersonIdent author = tag.getTaggerIdent();
                
                
                if ( author != null ) {
                    _lastTagAuthorName = StringUtils.defaultString( author.getName() );
                    _lastTagAuthorEmail = StringUtils.defaultString( author.getEmailAddress() );
                } else {
                    _lastTagAuthorName = "";
                    _lastTagAuthorEmail = "";
                }
                
            } else {
                _lastTagAuthorName = "";
                _lastTagAuthorEmail = "";
               
            }
            
            
        } else {
            _lastTagAuthorName = "";
            _lastTagAuthorEmail = "";
        }

        _displayString = makeDisplayString(currentBranch, lastCommit, workingCopyDirty, lastTag, lastTagDirty, getLastTagAuthorName());

    }

    static GitInfo valueOf( final String currentBranch, final String lastCommit, final boolean workingCopyDirty,
            final CustomTag lastTag, final boolean lastTagDirty, final String lastCommitShortHash, final Date lastCommitDate ) {
        return new GitInfo( currentBranch, lastCommit, workingCopyDirty, lastTagDirty, lastTag, lastCommitShortHash, lastCommitDate );
    }

    private static String makeDisplayString(final String currentBranch, final String lastCommit, final boolean workingCopyDirty,
            final CustomTag lastTag, final boolean lastTagDirty, final String lastTagAuthorName ) {
        final StringBuilder retval = new StringBuilder();
        retval.append( "Currently on branch " ).append( currentBranch ).append( " which has " ).append( workingCopyDirty ? "uncomitted changes" : "no changes").append('\n');
        retval.append( "Last Commit: " ).append( lastCommit ).append('\n');
        retval.append( "Last Tag: " ).append( lastTag == null  ? "unknown" : lastTag.getName() ).append( " by " ).append( StringUtils.isBlank( lastTagAuthorName ) ? "unknown" : lastTagAuthorName ).append( " which is " ).append( lastTagDirty ? "dirty" : "clean");

        return retval.toString();
    }

    String getDisplayString() {
        return _displayString;
    }

    String getCurrentBranch() {
        return _currentBranch;
    }

    String getLastCommit() {
        return _lastCommit;
    }

    String getLastCommitShort() {
        return _lastCommitShort;
    }

    Date getLastCommitDate() {
        return _lastCommitDate;
    }

    boolean isWorkingCopyDirty() {
        return _workingCopyDirty;
    }

    boolean isLastTagDirty() {
        return _lastTagDirty;
    }

    String getLastTagName() {
        return _lastTag == null ? "" : _lastTag.getName();
    }

    String getLastTagHash() {
        return _lastTag == null ? "" : _lastTag.getObject().name();
    }

    String getVersionPostfix() {
        if ( _workingCopyDirty ) {
            return SNAPSHOT_POSTFIX;
        }
        final StringBuilder retval = new StringBuilder();
        final String lastTagName = getLastTagName();
        if ( StringUtils.isNotBlank( lastTagName ) ) {
            retval.append(lastTagName);
        }
        if ( _lastTagDirty ) {
            if ( retval.length() > 0 ) {
                retval.append('-');
            }
            retval.append(getLastCommit()).append('-').append( SNAPSHOT_POSTFIX );
        }
        if ( retval.length() == 0 ) {
            retval.append(getLastCommit());
        }

        return retval.toString();
    }

    public String getLastTagAuthorName() {
        return _lastTagAuthorName;
    }

    public String getLastTagAuthorEmail() {
        return _lastTagAuthorEmail;
    }

}
