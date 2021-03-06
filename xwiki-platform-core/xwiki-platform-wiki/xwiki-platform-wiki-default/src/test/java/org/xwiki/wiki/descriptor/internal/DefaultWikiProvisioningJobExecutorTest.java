/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.xwiki.wiki.descriptor.internal;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.xwiki.context.Execution;
import org.xwiki.context.ExecutionContextManager;
import org.xwiki.job.Job;
import org.xwiki.test.mockito.MockitoComponentMockingRule;
import org.xwiki.wiki.internal.provisioning.DefaultWikiProvisioningJobExecutor;
import org.xwiki.wiki.provisioning.WikiProvisioningJob;
import org.xwiki.wiki.provisioning.WikiProvisioningJobRequest;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for {@link org.xwiki.wiki.internal.provisioning.DefaultWikiProvisioningJobExecutor}.
 *
 * @version $Id$
 * @since 5.3M2
 */
public class DefaultWikiProvisioningJobExecutorTest
{
    @Rule
    public MockitoComponentMockingRule<DefaultWikiProvisioningJobExecutor> mocker =
            new MockitoComponentMockingRule(DefaultWikiProvisioningJobExecutor.class);

    @Test
    public void createAndExecuteJob() throws Exception
    {
        // Mocks
        WikiProvisioningJob provisioningJob = mock(WikiProvisioningJob.class);
        mocker.registerComponent(Job.class, "wikiprovisioning.test", provisioningJob);
        ExecutionContextManager executionContextManager = mock(ExecutionContextManager.class);
        mocker.registerComponent(ExecutionContextManager.class, executionContextManager);
        Execution execution = mock(Execution.class);
        mocker.registerComponent(Execution.class, execution);

        // Execute
        WikiProvisioningJob job = mocker.getComponentUnderTest().createAndExecuteJob("wikiid", "wikiprovisioning.test",
                "templateid");

        // Verify
        // Id of the job.
        List<String> jobId = new ArrayList<String>();
        jobId.add("wiki");
        jobId.add("provisioning");
        jobId.add("wikiprovisioning.test");
        jobId.add("wikiid");
        verify(provisioningJob).initialize(eq(new WikiProvisioningJobRequest(jobId, "wikiid", "templateid")));
        Thread.sleep(100);
        verify(provisioningJob).run();

        assertEquals(mocker.getComponentUnderTest().getJob(jobId), job);
    }
}
